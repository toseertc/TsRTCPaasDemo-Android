package cn.tosee.rtch264demo.customvideo;

import android.content.Context;
import android.os.SystemClock;

import cn.tosee.rtc.capture.CameraFactory;
import cn.tosee.rtc.capture.CameraVideoCapturer;
import cn.tosee.rtc.capture.CapturerObserver;
import cn.tosee.rtc.capture.EglBase;
import cn.tosee.rtc.capture.JavaI420Buffer;
import cn.tosee.rtc.capture.SurfaceTextureHelper;
import cn.tosee.rtc.capture.VideoCapturer;
import cn.tosee.rtc.capture.VideoFrame;
import cn.tosee.rtc.capture.YuvHelper;
import cn.tosee.rtc.internal.PermissionsProcess;
import cn.tosee.rtc.log.TSLog;
import cn.tosee.rtc.video.IVideoFrameConsumer;
import cn.tosee.rtc.video.IVideoSink;
import cn.tosee.rtc.video.IVideoSource;
import cn.tosee.rtch264demo.codec.HwAvcEncoder;
import cn.tosee.rtch264demo.constant.VIDEO_STREAM_TYPE;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class CustomVideoSource implements IVideoSource {
    public static final String TAG = "VideoSource";
    public static final String VIDEO_CAPTURER_THREAD_NAME = "VideoCapturerThread";

    public Context mAppContext;
    public static EglBase mEglBase;

    public VideoCapturer mVideoCapturer;
    public SurfaceTextureHelper mSurfaceTextureHelper;
    public boolean mVideoCapturerStopped;
    public int width = 1280, height = 720, fps = 30;//暂时设置为30fps
    private volatile IVideoFrameConsumer consumer;
    private boolean mShouldCapture = false;//是否开始采集，主要考虑同时只能开启一次采集；第二路通过拷贝处理；不进行采集
    private List<IVideoSink> captureVideoSinkList = new ArrayList<>();//本地采集的渲染接口
    private byte[] data;
    private byte[] dstdata;
    private byte[] dstdata2;
    VideoFrame.I420Buffer srcBuffer;
    JavaI420Buffer i420Buffer;
    ByteBuffer dstBuffer;
    HwAvcEncoder encoder;
    boolean shouldEncode = true;
    boolean encodeInit=false;
    EncodeErrorInterface encodeErrorInterface;

    public void addCaptureVideoSink(IVideoSink captureVideoSink) {
        captureVideoSinkList.add(captureVideoSink);
    }

    public void removeCaptureVideoSink(IVideoSink captureVideoSink) {
        if (captureVideoSinkList.contains(captureVideoSink)) {
            captureVideoSinkList.remove(captureVideoSink);
        }

    }
    public void setEncodeErrorInterface(EncodeErrorInterface encodeErrorInterface) {
        this.encodeErrorInterface = encodeErrorInterface;
    }

    public CustomVideoSource(final Context appContext, boolean shouldEncode, boolean shouldCapture, CameraVideoCapturer.CameraEventsHandler eventsHandler) {
        this.shouldEncode = shouldEncode;
        this.mShouldCapture = shouldCapture;
        mAppContext = appContext;
        if (!mShouldCapture) {
            return;
        }
        if (shouldEncode) {
            encoder = new HwAvcEncoder((data, length, isKey, timestamp) -> {
                if (consumer != null) {
                    consumer.consumeVideoPacket(data, length, VIDEO_STREAM_TYPE.VIDEO_STREAM_H264, isKey, timestamp);
                }
            });
        }
        mEglBase = EglBase.create();
        EglBase.Context eglContext = mEglBase.getEglBaseContext();
        mSurfaceTextureHelper = SurfaceTextureHelper.create(VIDEO_CAPTURER_THREAD_NAME, eglContext);
        mVideoCapturer = CameraFactory.createVideoCapture(mAppContext, eventsHandler);
        mVideoCapturer.initialize(mSurfaceTextureHelper, mAppContext,
                new CapturerObserver() {
                    @Override
                    public void onCapturerStarted(boolean success) {
//                        capturerObserver.onCapturerStarted(success);
                    }

                    @Override
                    public void onCapturerStopped() {
//                        capturerObserver.onCapturerStopped();
                    }

                    @Override
                    public void onFrameCaptured(VideoFrame frame) {
                        if (consumer == null) {
                            return;
                        }
                        srcBuffer = frame.getBuffer().toI420();

                        i420Buffer = JavaI420Buffer.allocate(frame.getRotatedWidth(), frame.getRotatedHeight());
                        YuvHelper.I420Rotate(
                                srcBuffer.getDataY(), srcBuffer.getStrideY(),
                                srcBuffer.getDataU(), srcBuffer.getStrideU(),
                                srcBuffer.getDataV(), srcBuffer.getStrideV(),
                                i420Buffer.getDataY(), i420Buffer.getStrideY(),
                                i420Buffer.getDataU(), i420Buffer.getStrideU(),
                                i420Buffer.getDataV(), i420Buffer.getStrideV(),
                                srcBuffer.getWidth(), srcBuffer.getHeight(),
                                frame.getRotation());

                        dstBuffer = ByteBuffer.allocateDirect(i420Buffer.getWidth() * i420Buffer.getHeight() * 3 / 2);
                        fillBuffer(dstBuffer, i420Buffer, frame.getRotation());
                        //在前置摄像头的时候处理镜像，即旋转角度为270°时
                        data = new byte[i420Buffer.getWidth() * i420Buffer.getHeight() * 3 / 2];
                        dstdata = new byte[i420Buffer.getWidth() * i420Buffer.getHeight() * 3 / 2];
                        dstdata2 = new byte[i420Buffer.getWidth() * i420Buffer.getHeight() * 3 / 2];
                        dstBuffer.position(0);
                        dstBuffer.get(data);
                        System.arraycopy(data, 0, dstdata, 0, data.length);
                        System.arraycopy(data, 0, dstdata2, 0, data.length);
                        if (shouldEncode) {
                            if (encoder != null) {
                                encoder.encode(i420Buffer.getDataY(), i420Buffer.getDataU(), i420Buffer.getDataV(),
                                        i420Buffer.getStrideY(), i420Buffer.getStrideU(),
                                        i420Buffer.getStrideV(), i420Buffer.getWidth(),
                                        i420Buffer.getHeight(), SystemClock.elapsedRealtime());
                            }
                        } else {
                            consumer.consumeVideoData(data, 0, frame.getRotatedWidth(), frame.getRotatedHeight(), SystemClock.elapsedRealtime());
                        }

                        for (IVideoSink iVideoSink : captureVideoSinkList) {//渲染
                            if (iVideoSink != null) {
                                iVideoSink.consumeVideoData(dstdata, 0, frame.getRotatedWidth(), frame.getRotatedHeight(), SystemClock.elapsedRealtime());
                            }
                        }

                        i420Buffer.release();
                        frame.release();
                        srcBuffer.release();
                    }
                });

    }

    void fillBuffer(ByteBuffer dstBuffer, VideoFrame.I420Buffer i420, int rotationMode) {
        YuvHelper.I420Copy(i420.getDataY(), i420.getStrideY(), i420.getDataU(), i420.getStrideU(),
                i420.getDataV(), i420.getStrideV(), dstBuffer, i420.getWidth(), i420.getHeight());


    }

    public void setVideoConfig(int width, int height, int fps) {
        this.width = width;
        this.height = height;
    }


    public int start() {
        if (!mShouldCapture) {
            return 0;
        }
        if (!PermissionsProcess.checkCameraPermission(mAppContext)) {
            return 1052;
        }
        if (shouldEncode) {
            if (!encodeInit) {
                int result=encoder.start(width, height, 15, 1200000);
                if (result<0) {
                    if (encodeErrorInterface!=null) {
                        encodeErrorInterface.onError();
                    }
                }
                encodeInit=true;
            }

        }
        mVideoCapturerStopped = false;
        mVideoCapturer.startCapture(width, height, fps);
        return 0;
    }


    public void changeCaptureFormat() {
        if (!mShouldCapture) {
            return;
        }
        //改变分辨率需要先调用setVideoConfig再调用这个方法
        mVideoCapturerStopped = false;
        mVideoCapturer.changeCaptureFormat(width, height, fps);
    }


    public void switchCamera() {
        if (!mShouldCapture) {
            return;
        }
        if (mVideoCapturer instanceof CameraVideoCapturer) {
            ((CameraVideoCapturer) mVideoCapturer).switchCamera(null);
        }
    }


    public void stop() {
        if (!mShouldCapture) {
            return;
        }

        if (mVideoCapturer != null && !mVideoCapturerStopped) {
            try {
                mVideoCapturer.stopCapture();
            } catch (InterruptedException e) {
            }
            mVideoCapturerStopped = true;
        }
        if (shouldEncode) {
            if (encoder!=null) {
            encoder.release();
            encoder=null;
            }
        }
    }

    public static EglBase getRootEglBase() {
        return mEglBase;
    }


    public void destroy() {
        consumer = null;
        if (!mShouldCapture) {
            return;
        }
        mVideoCapturer.dispose();
        mSurfaceTextureHelper.dispose();
        mEglBase.release();
        mEglBase = null;
    }

    @Override
    public boolean onInitialize(IVideoFrameConsumer iVideoFrameConsumer) {
        this.consumer = iVideoFrameConsumer;
        return true;
    }

    @Override
    public boolean onLowStreamReady(IVideoFrameConsumer iVideoFrameConsumer) {
        return false;
    }

    @Override
    public void onDispose() {
        destroy();
    }

    @Override
    public int onStart() {
        TSLog.e("ygscustom--->CustomVideoSource onstart" + this);
        return start();
    }

    @Override
    public void onStop() {
        TSLog.e("ygscustom--->CustomVideoSource onstop" + this);
        stop();
    }

    public interface EncodeErrorInterface{
        void onError();
    }
}
