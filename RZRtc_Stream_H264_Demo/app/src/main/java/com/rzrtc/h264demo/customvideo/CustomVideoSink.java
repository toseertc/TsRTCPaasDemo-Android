package com.rzrtc.h264demo.customvideo;


import android.util.Log;

import com.rz.paas.capture.JavaI420Buffer;
import com.rz.paas.capture.JniCommon;
import com.rz.paas.capture.VideoFrame;
import com.rz.paas.capture.YuvHelper;
import com.rz.paas.gl.GLFrameRenderer;
import com.rz.paas.utils.Constant;
import com.rz.paas.video.GLFrameSurfaceView;
import com.rz.paas.video.IVideoSink;
import com.rz.paas.video.bean.VideoSinkConfig;

import java.nio.ByteBuffer;

import com.rzrtc.h264demo.codec.HwAvcDecoder;
import com.rzrtc.h264demo.constant.VIDEO_PIXEL_FORMAT;
import com.rzrtc.h264demo.constant.VIDEO_STREAM_TYPE;
import com.rzrtc.parseh264.H264Utils;

/**
 * @author : create by yanggaosheng$
 * @time : 2020/10/12$
 * @des :  $
 **/
public class CustomVideoSink implements IVideoSink {
    public GLFrameSurfaceView surfaceView;
    public GLFrameRenderer renderer;
    protected int renderMode = 2;//1：按比列渲染，0：填充，2：按比例拉伸
    protected int mirrorMode = 1;

    private boolean shouldDecode = true;
    private HwAvcDecoder decoder;
    H264Utils h264Utils;
    public CustomVideoSink(boolean shouldDecode) {
        this.shouldDecode = shouldDecode;
        h264Utils = new H264Utils();
        if (shouldDecode) {
            decoder = new HwAvcDecoder(new HwAvcDecoder.FrameCallback() {
                @Override
                public void frameData(ByteBuffer dataY, ByteBuffer dataU, ByteBuffer dataV, int strideY, int strideU, int strideV, int width, int height, long timestamp) {
                    playVideoData(dataY,dataU,dataV,width,height);
                }
            });
        }
    }


    public void setVideoRenderMode(int renderMode) {
        if (renderMode <= 0) {
            this.renderMode = Constant.RENDER_MODE_FIT;

        } else {
            this.renderMode = renderMode;
        }
        if (renderer != null) {
            renderer.setMode(this.renderMode, this.mirrorMode);
        }
    }

    public void setMirrorMode(int mirrorMode) {
        if (mirrorMode <= 0) {
            this.mirrorMode = Constant.VIDEO_MIRROR_MODE_ENABLED;
//            return;
        } else {
            this.mirrorMode = mirrorMode;
        }
        if (renderer != null) {
            renderer.setMode(this.renderMode, this.mirrorMode);
        }
    }


    public void setSurfaceView(GLFrameSurfaceView surfaceView) {
        if (surfaceView == null) {
            return;
        }
        this.surfaceView = surfaceView;
        this.renderer = this.surfaceView.getRender();
        if (renderer != null) {
            renderer.setMode(renderMode, mirrorMode);
        }

    }


    public void playVideoData(ByteBuffer bufferY, ByteBuffer bufferU, ByteBuffer bufferV, int width, int height) {
        if (renderer == null) {
            return;
        }
        if (!renderer.isInitSuccess()) {
            return;
        }
        if (renderer.getWidth() != width || renderer.getHeight() != height) {
            renderer.setWidth_Height(width, height);
            renderer.update(width, height);
        }

        renderer.update(bufferY, bufferU, bufferV, null);
    }


    public void destroy() {
        if (renderer != null) {
            renderer.releaseFinal();
        }
        if (surfaceView != null) {
            surfaceView = null;
        }
    }

    @Override
    public boolean onInitialize() {
        return false;
    }

    @Override
    public boolean onStart() {
        if (shouldDecode) {
            if (decoder!=null) {
                decoder.start(1280, 720);
            }
        }
        return false;
    }

    @Override
    public void onStop() {

    }

    @Override
    public void onDispose() {
        if (shouldDecode) {
            if (decoder!=null) {
                decoder.release();
            }
        }
        destroy();
    }

    @Override
    public VideoSinkConfig getSinkConfig() {
        VideoSinkConfig videoSinkConfig = new VideoSinkConfig();
        videoSinkConfig.streamType = shouldDecode ? VIDEO_STREAM_TYPE.VIDEO_STREAM_H264 : VIDEO_STREAM_TYPE.VIDEO_STREAM_FRAME;
        videoSinkConfig.pixelFormat = shouldDecode ? VIDEO_PIXEL_FORMAT.VIDEO_PIXEL_UNAVILABLE : VIDEO_PIXEL_FORMAT.VIDEO_PIXEL_I420;
        return videoSinkConfig;
    }

    ByteBuffer i420ByteBuffer;
    VideoFrame.I420Buffer i420Buffer;

    @Override
    public void consumeVideoData(byte[] buffer, int format, int width, int height, long timestamp) {

        int stride = buffer.length * 2 / (height * 3);
        i420ByteBuffer = JniCommon.nativeAllocateByteBuffer(stride * height * 3 / 2);


        VideoFrame.Buffer vbuffer = copyI420Buffer(i420ByteBuffer.put(buffer), stride, height, width, height);

        i420Buffer = vbuffer.toI420();

        playVideoData(i420Buffer.getDataY(), i420Buffer.getDataU(), i420Buffer.getDataV(), width, height);

        i420Buffer.release();
        vbuffer.release();
        JniCommon.nativeFreeByteBuffer(i420ByteBuffer);
    }

    ByteBuffer wrap ;
    int width=0;
    int height=0;
    @Override
    public void consumeVideoPacket(byte[] buffer, int length, int streamType, boolean isKey, long timestamp) {
        if (shouldDecode) {
            if (decoder!=null) {
                if (h264Utils.getWidth_Height_fromPacket(buffer,length)) {
                    width=h264Utils.getWidth();
                    height = h264Utils.getHeight();
                }
                Log.e("yyconsumeVideoPacket-->", "consumeVideoPacket: "+width+" height="+height );
                 wrap = ByteBuffer.wrap(buffer);
                decoder.decodeFrame(wrap,width,height,timestamp);
            }
        }
    }

    private VideoFrame.Buffer copyI420Buffer(
            ByteBuffer buffer, int stride, int sliceHeight, int width, int height) {
        if (stride % 2 != 0) {
            throw new AssertionError("Stride is not divisible by two: " + stride);
        }

        final int chromaWidth = (width + 1) / 2;
        final int chromaHeight = (sliceHeight % 2 == 0) ? (height + 1) / 2 : height / 2;

        final int uvStride = stride / 2;

        final int yPos = 0;
        final int yEnd = yPos + stride * height;
        final int uPos = yPos + stride * sliceHeight;
        final int uEnd = uPos + uvStride * chromaHeight;
        final int vPos = uPos + uvStride * sliceHeight / 2;
        final int vEnd = vPos + uvStride * chromaHeight;

        VideoFrame.I420Buffer frameBuffer = allocateI420Buffer(width, height);

        buffer.limit(yEnd);
        buffer.position(yPos);
        copyPlane(buffer.slice(), stride, frameBuffer.getDataY(), frameBuffer.getStrideY(), width, height);

        buffer.limit(uEnd);
        buffer.position(uPos);
        copyPlane(buffer.slice(), uvStride, frameBuffer.getDataU(), frameBuffer.getStrideU(),
                chromaWidth, chromaHeight);
        if (sliceHeight % 2 == 1) {
            buffer.position(uPos + uvStride * (chromaHeight - 1)); // Seek to beginning of last full row.

            ByteBuffer dataU = frameBuffer.getDataU();
            dataU.position(frameBuffer.getStrideU() * chromaHeight); // Seek to beginning of last row.
            dataU.put(buffer); // Copy the last row.
        }

        buffer.limit(vEnd);
        buffer.position(vPos);
        copyPlane(buffer.slice(), uvStride, frameBuffer.getDataV(), frameBuffer.getStrideV(),
                chromaWidth, chromaHeight);
        if (sliceHeight % 2 == 1) {
            buffer.position(vPos + uvStride * (chromaHeight - 1)); // Seek to beginning of last full row.

            ByteBuffer dataV = frameBuffer.getDataV();
            dataV.position(frameBuffer.getStrideV() * chromaHeight); // Seek to beginning of last row.
            dataV.put(buffer); // Copy the last row.
        }

        return frameBuffer;
    }

    protected VideoFrame.I420Buffer allocateI420Buffer(int width, int height) {
        return JavaI420Buffer.allocate(width, height);
    }

    protected void copyPlane(
            ByteBuffer src, int srcStride, ByteBuffer dst, int dstStride, int width, int height) {
        YuvHelper.copyPlane(src, srcStride, dst, dstStride, width, height);
    }
}
