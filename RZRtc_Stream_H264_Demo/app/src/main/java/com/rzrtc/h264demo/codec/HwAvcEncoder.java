package com.rzrtc.h264demo.codec;

import android.media.MediaCodecInfo;
import android.os.Build;
import android.util.Log;

import com.rz.paas.capture.EglBase14;
import com.rz.paas.capture.JavaI420Buffer;
import com.rz.paas.capture.VideoFrame;
import com.rz.paas.codec.AndroidVideoEncoder;
import com.rz.paas.codec.BaseBitrateAdjuster;
import com.rz.paas.codec.DBCodecUtils;
import com.rz.paas.codec.DBVideoCodecStatus;
import com.rz.paas.codec.EnCodecCallback;
import com.rz.paas.codec.EncodedImage;
import com.rz.paas.log.RZLog;
import com.rz.paas.utils.Constant;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class HwAvcEncoder implements EnCodecCallback {
    private static final String TAG = "HwAvcEncoder";
    private AndroidVideoEncoder mVideoEncoder;

    private EglBase14.Context sharedContext;

    H264DataCallbac h264DataCallbac;

    public HwAvcEncoder(H264DataCallbac callbac) {
        this.h264DataCallbac = callbac;
    }

    public int start(int width, int height, int frameRate, int bitrate) {
        //todo sharedContext赋值？？？？
        int code = -1;
        Class clz = null;


        this.sharedContext = null;// (EglBase14.Context) VideoSource.getRootEglBase().getEglBaseContext();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return Constant.WARN_VCM_ENCODER_HW_FAILED;
        }
        DBCodecUtils.VideoCodecMimeType type = DBCodecUtils.VideoCodecMimeType.valueOf(DBCodecUtils.VideoCodecMimeType.H264.name());
        MediaCodecInfo info = DBCodecUtils.findCodecForType(type);
        if (info == null) {
            RZLog.e("can't open start hwAvcEncoder");
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
        DBVideoCodecStatus dbVideoCodecStatus = mVideoEncoder.initEncode(width, height, frameRate, bitrate,0, HwAvcEncoder.this);
        code = dbVideoCodecStatus.getNumber();

        return code;
    }


    public int renderFrame(final VideoFrame frame) {
        if (mVideoEncoder != null) {
            DBVideoCodecStatus encode = mVideoEncoder.encode(frame, false);
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
        if (h264DataCallbac != null) {
            packetData = new byte[image.buffer.limit()];
            image.buffer.get(packetData);
            h264DataCallbac.onH264Packet(packetData, image.buffer.limit(), image.frameType.getNative() == 3, image.captureTimeNs);
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

    public interface H264DataCallbac {
        void onH264Packet(byte[] data, int length, boolean isKey, long timestamp);
    }
}
