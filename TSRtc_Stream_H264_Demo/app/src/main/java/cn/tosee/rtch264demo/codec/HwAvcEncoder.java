package cn.tosee.rtch264demo.codec;

import android.media.MediaCodecInfo;
import android.os.Build;
import android.util.Log;

import cn.tosee.rtc.capture.EglBase14;
import cn.tosee.rtc.capture.JavaI420Buffer;
import cn.tosee.rtc.capture.VideoFrame;
import cn.tosee.rtc.codec.AndroidVideoEncoder;
import cn.tosee.rtc.codec.BaseBitrateAdjuster;
import cn.tosee.rtc.codec.TSCodecUtils;
import cn.tosee.rtc.codec.TSVideoCodecStatus;
import cn.tosee.rtc.codec.EnCodecCallback;
import cn.tosee.rtc.codec.EncodedImage;
import cn.tosee.rtc.log.TSLog;
import cn.tosee.rtc.utils.Constant;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class HwAvcEncoder implements EnCodecCallback {
    private static final String TAG = "HwAvcEncoder";
    private AndroidVideoEncoder mVideoEncoder;

    private EglBase14.Context sharedContext;

    H264DataCallback h264DataCallback;

    public HwAvcEncoder(H264DataCallback callback) {
        this.h264DataCallback = callback;
    }

    public int start(int width, int height, int frameRate, int bitrate) {
        //todo sharedContext赋值？？？？
        int code = -1;
        Class clz = null;


        this.sharedContext = null;// (EglBase14.Context) VideoSource.getRootEglBase().getEglBaseContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Constant.WARN_VCM_ENCODER_HW_FAILED;
        }
        TSCodecUtils.VideoCodecMimeType type = TSCodecUtils.VideoCodecMimeType.valueOf(TSCodecUtils.VideoCodecMimeType.H264.name());
        MediaCodecInfo info = TSCodecUtils.findCodecForType(type);
        if (info == null) {
            TSLog.e("can't open start hwAvcEncoder");
            return Constant.WARN_VCM_ENCODER_HW_FAILED;
        }
        String codecName = info.getName();
        String mime = "video/avc";
        Integer surfaceColorFormat = selectColorFormat(getTextureColorFormats(), info.getCapabilitiesForType(mime));
        Integer yuvColorFormat = selectColorFormat(
                ENCODER_COLOR_FORMATS, info.getCapabilitiesForType(mime));

        mVideoEncoder = new AndroidVideoEncoder(codecName, type,
                surfaceColorFormat, yuvColorFormat, new HashMap<String, String>(), 2, new BaseBitrateAdjuster(),
                sharedContext);
        TSVideoCodecStatus TSVideoCodecStatus = mVideoEncoder.initEncode(width, height, frameRate, bitrate,2, HwAvcEncoder.this);
        code = TSVideoCodecStatus.getNumber();

        return code;
    }


    public int renderFrame(final VideoFrame frame) {
        if (mVideoEncoder != null) {
            TSVideoCodecStatus encode = mVideoEncoder.encode(frame, false);
            return encode.getNumber();
        }
        return 0;
    }

    JavaI420Buffer wrap;
    VideoFrame frame;

    public int encode(ByteBuffer dataY, ByteBuffer dataU, ByteBuffer dataV,
                      int strideY, int strideU, int strideV, int width, int height, long timestamp) {
        wrap = JavaI420Buffer.wrap(width, height, dataY, strideY, dataU, strideU, dataV, strideV, null);
        frame = new VideoFrame(wrap, 0, timestamp);
        int result = renderFrame(frame);
        frame.release();
        wrap.release();
        return result;
    }

    public void release() {
        mVideoEncoder.release();

    }

    private byte[] packetData;

    @Override
    public void onCodecFrame(EncodedImage image) {
        if (h264DataCallback != null) {
            packetData = new byte[image.buffer.limit()];
            image.buffer.get(packetData);
            h264DataCallback.onH264Packet(packetData, image.buffer.limit(), image.frameType.getNative() == 3, image.captureTimeNs);
        }
        Log.e(TAG, "onCodecFrame: ok");
    }

    @Override
    public void onEncodeError() {
        Log.e(TAG, "onCodecFrame: error");
    }

    private static int[] getTextureColorFormats() {
        if (Build.VERSION.SDK_INT >= 18) {
            return new int[]{MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface};
        } else {
            return new int[]{};
        }
    }

    static Integer selectColorFormat(
            int[] supportedColorFormats, MediaCodecInfo.CodecCapabilities capabilities) {
        for (int supportedColorFormat : supportedColorFormats) {
            for (int codecColorFormat : capabilities.colorFormats) {
                if (codecColorFormat == supportedColorFormat) {
                    return codecColorFormat;
                }
            }
        }
        return null;
    }

    static final int[] ENCODER_COLOR_FORMATS = {
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
            MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
            MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
            0x7FA30C04};

    public interface H264DataCallback {
        void onH264Packet(byte[] data, int length, boolean isKey, long timestamp);
    }
}
