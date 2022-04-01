package cn.tosee.rtch264demo.rtc.listener

import cn.tosee.rtc.IRtcChannel
import cn.tosee.rtc.stats.RemoteAudioStats
import cn.tosee.rtc.stats.RemoteVideoStats
import cn.tosee.rtc.stats.RtcStats

abstract class MiddleRtcChannelEventHandler {
    /**
     * 加入频道回调。
     */
    open fun onJoinChannelSuccess(channel: IRtcChannel, userID: String, elapsed: Int) {}

    /**
     * 重新加入频道回调。
     */
    open fun onRejoinChannelSuccess(channel: IRtcChannel, userID: String, elapsed: Int) {}

    /**
     * 离开频道回调。
     */
    open fun onLeaveChannel(channel: IRtcChannel, stats: RtcStats) {}

    /**
     * 直播场景下用户角色已切换回调。
     */
    open fun onClientRoleChanged(channel: IRtcChannel, oldRole: Int, newRole: Int, elapsed: Int) {}

    /**
     * 远端用户（通信场景）/主播（直播场景）加入当前频道回调。
     */
    open fun onUserJoined(channel: IRtcChannel, userId: String, elapsed: Int) {}

    /**
     * 远端用户（通信场景）/主播（直播场景）离开当前频道回调。
     */
    open fun onUserOffline(channel: IRtcChannel, userID: String, reason: Int) {}

    /**
     * 本地音频推送状态发生改变
     */
    open fun onAudioPublishStateChanged(state: Int, elapsed: Int) {}

    /**
     * 本地视频推送状态发生改变
     */
    open fun onVideoPublishStateChanged(state: Int, elapsed: Int) {}

    /**
     *接收远端音频的相关状态发生改变，一般通过该回调处理音频的状态
     */
    open fun onAudioSubscribeStateChanged(userID: String, oldState: Int, elapsed: Int) {}

    /**
     *接收远端视频的相关状态发生改变，一般通过该回调处理视频的状态
     */
    open fun onVideoSubscribeStateChanged(
        userID: String, streamName: String, oldState: Int,
        elapsed: Int
    ) {
    }

    /**
     * 已显示首帧远端视频回调。
     */
    open fun onFirstRemoteVideoFrame(
        userID: String, streamName: String, width: Int, height: Int, elapsed: Int
    ) {
    }

    /**
     * 远端视频大小和旋转信息发生改变回调。
     */
    open fun onVideoSizeChanged(
        userID: String, streamName: String, width: Int, height: Int, elapsed: Int
    ) {
    }

    /**
     * 频道内发生一些警告信息的回调
     */
    open fun onWarning(channel: IRtcChannel?, warn: Int, msg: String?) {};
    /**
     * 频道内发生一些错误信息的回调
     * 一般情况下，需要关注该错误；并通过错误去做相应的处理
     */
    open fun onError(channel: IRtcChannel?, err: Int, msg: String?) {}

    /**
     * 频道内远端用户的网络质量回调，只有音视频上线后才会触发该回调
     */
    open fun onNetworkQuality(
        channel: IRtcChannel?,
        userID: String?,
        txQuality: Int,
        rxQuality: Int
    ) {
    }

    /**
     *频道内连接丢失的回调，如果在频道内触发该回调；一般由网络原因导致；需要重新进入频道
     */
    open fun onConnectionLost(channel: IRtcChannel?) {}

    /**
     * 网络连接状态的相关状态的回调
     */
    open fun onConnectionStateChanged(channel: IRtcChannel?, state: Int, reason: Int) {}

    /**
     *
     * stats 的为SDK的相关统计信息的事件回调
     */

    open fun onRemoteAudioStats(channel: IRtcChannel, stats: RemoteAudioStats) {}

    open fun onRemoteVideoStats(channel: IRtcChannel, stats: RemoteVideoStats) {}

    open fun onRtcStats(channel: IRtcChannel, stats: RtcStats) {}

    /**
     *
     * @param newBitrate 预测的新的码率, int类型
     * @param isLowVideo 是否为小流
     *
     */
    open fun onPredictedBitrateChanged(uid: String?, streamName: String?, newBitrate: Int, isLowVideo: Boolean){}
}