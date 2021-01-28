package com.rzrtc.h264demo.codec;

import android.media.MediaCodecInfo;
import android.os.Build;

import com.rz.paas.annotation.CalledByNative;
import com.rz.paas.capture.EglBase;
import com.rz.paas.capture.VideoFrame;
import com.rz.paas.codec.AndroidVideoDecoder;
import com.rz.paas.codec.DBCodecUtils;
import com.rz.paas.codec.DBVideoCodecStatus;
import com.rz.paas.codec.DeCodecCallback;
import com.rz.paas.utils.Constant;
import com.rzrtc.parseh264.H264Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;

public class HwAvcDecoder implements DeCodecCallback {

    private AndroidVideoDecoder decoder;
    FrameCallback yuvFramecallback;

    @CalledByNative
    public HwAvcDecoder(FrameCallback callback) {
        this.yuvFramecallback = callback;

    }

    @CalledByNative
    public int start(int w, int h) {

        MediaCodecInfo info = DBCodecUtils.findCodecForType(DBCodecUtils.VideoCodecMimeType.H264);
        if (info == null) {
            return Constant.WARN_VCM_DECODER_HW_FAILED;//切换为软解
        }
        MediaCodecInfo.CodecCapabilities capabilities = info.getCapabilitiesForType("video/avc");
        decoder = new AndroidVideoDecoder(info.getName(),
                DBCodecUtils.VideoCodecMimeType.H264, selectColorFormat(DBCodecUtils.DECODER_COLOR_FORMATS, capabilities), null);
        DBVideoCodecStatus result = decoder.initDecode(w, h, HwAvcDecoder.this);

        return result.getNumber();

    }

    @CalledByNative
    public void decodeFrame(ByteBuffer h264buffer, int w, int h, long timestamp) {

        if (decoder != null) {
            decoder.decode(h264buffer, w, h, timestamp);

        }
    }

    @CalledByNative
    public void release() {
        if (decoder != null) {
            decoder.release();
        }


    }

    @Override
    public void onCodecFrame(VideoFrame.I420Buffer image, long timestamp) {
        if (yuvFramecallback != null) {
            yuvFramecallback.frameData(image.getDataY(), image.getDataU(), image.getDataV(), image.getStrideY(), image.getStrideU(), image.getStrideV(),
                    image.getWidth(), image.getHeight(), timestamp);
        }

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


    public interface FrameCallback {
        void frameData(ByteBuffer dataY, ByteBuffer dataU, ByteBuffer dataV,
                       int strideY, int strideU, int strideV, int width, int height, long timestamp);
    }


}
