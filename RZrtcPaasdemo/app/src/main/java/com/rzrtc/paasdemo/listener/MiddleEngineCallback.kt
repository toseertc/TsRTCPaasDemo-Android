package com.rz.paas.test.pass_sdk.middle

import com.rz.paas.stats.*

abstract class MiddleEngineCallback {
    open fun onWarning(warn: Int, msg: String) {}
    open fun onError(err: Int, msg: String) {}
    open fun onLocalVideoStateChanged(state: Int, error: Int) {}
    open fun onLocalAudioStateChanged(state: Int, error: Int) {}

    //远端音频回调
    open fun onAudioVolumeIndication(
        speakers: Array<AudioVolumeInfo>?,
        totalVolume: Int
    ) {
    }

    //elapsed 时间戳在录制时有用
    open fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
    }

    open fun onFirstLocalVideoFramePublished(elapsed: Int) {}

    //移动端不用管下面两个函数
    open fun onMediaDeviceStateChanged(deviceId: String, deviceType: Int, deviceState: Int) {}

    open fun onAudioDeviceVolumeChanged(deviceType: Int, volume: Int, muted: Boolean) {}

    open fun onVideoSizeChanged(width: Int, height: Int) {}

    //本地音频第一帧推送成功
    open fun onFirstLocalAudioFramePublished(elapsed: Int) {}

    //音频路线切换
    open fun onAudioRouteChanged(routing: Int) {}


    //网络状态改变
    open fun onNetworkTypeChanged(type: Int) {}


    //本地音频状态变化
    open fun onLocalAudioStats(stats: LocalAudioStats) {}


    //本地视频状态变化
    open fun onLocalVideoStats(stats: LocalVideoStats) {}


    //首页网络质量检测
    open fun onLastmileQuality(quality: Int) {}

    //最后一公里测速结果(到流媒体的边缘服务器的测速结果,周期性回调)
    open fun onLastmileProbeResult(result: LastmileProbeResult) {}

    open fun onLocalNetworkQuality(txQuality: Int, rxQuality: Int) {}

}