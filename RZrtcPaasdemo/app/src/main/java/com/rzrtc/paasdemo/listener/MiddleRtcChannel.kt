package com.rz.paas.test.pass_sdk.middle

import android.util.Log
import com.rz.paas.IRtcChannel
import com.rz.paas.stream.IRtcStream
import com.rz.paas.stream.IRtcStreamEventHandler
import com.rz.paas.video.IVideoSink
import com.rz.paas.video.IVideoSource
import com.rz.paas.video.VideoEncoderConfiguration
import com.rz.paas.video.bean.VideoCanvas
import com.rzrtc.paasdemo.const.PUBLISH_MEDIA_TYPE


class MiddleRtcChannel(val rtcChannel: IRtcChannel) {
    private  final val TAG="PaaSInstance"
    /**
     * 设置用户角色。
     *
     * @param role
     * @return 0(ERR_OK): 方法调用成功。
     * < 0: 方法调用失败。
     * -1(ERR_FAILED): 一般性的错误（未明确归类）。
     * * -2(ERR_INALID_ARGUMENT): 参数无效。
     * * -7(ERR_NOT_INITIALIZED): SDK 尚未初始化。
     */
    fun setClientRole(role: Int): Int {
        val result = rtcChannel.setClientRole(role)
        Log.e(TAG,"IRtcChannel.setClientRole(${role}) result=${result}")
        return result
    }

    /**
     * 自定义视频渲染
     */
    fun setRemoteVideoSink(userId: String, streamName: String?, sink: IVideoSink): Int {
        val result = rtcChannel.setRemoteVideoSink(userId, streamName, sink)
        Log.e(TAG,"IRtcChannel.setRemoteVideoSink(${userId},${streamName},sink) result=${result}")
        return result

    }

    /**
     * 加入频道。
     *
     * @param userID 用户ID 标识用户
     * @return 0(ERR_OK): 方法调用成功。
     * < 0: 方法调用失败。
     * -2(ERR_INALID_ARGUMENT): 参数无效。
     * -3(ERR_NOT_READY): SDK 初始化失败，请尝试重新初始化 SDK。
     * -5(ERR_REFUSED): 调用被拒绝。可能有如下两个原因：
     * 已经加入了 channelID 频道。
     */
    fun joinChannel(userID: String): Int {
        val result = rtcChannel.joinChannel(userID)
        Log.e(TAG,"IRtcChannel.joinChannel(${userID}) result=${result}")
        return result

    }


    /**
     * 离开频道。
     *
     * @return 0(ERR_OK): 方法调用成功。
     * < 0: 方法调用失败。
     * -1(ERR_FAILED): 一般性的错误（未明确归类）。
     * -2(ERR_INALID_ARGUMENT): 参数无效。
     * -7(ERR_NOT_INITIALIZED): SDK 尚未初始化。
     */
    fun leaveChannel(): Int {
        val result = rtcChannel.leaveChannel()
        Log.e(TAG,"IRtcChannel.leaveChannel) result=${result}")
        return result

    }

    /**
     * 增加一路视频推流
     *
     * @param streamName  流名称
     * @param videoSource 视频源
     * @param videoCfg    编解码配置
     * @return
     */
    //大小流配置
    fun createVideoStream(
            streamName: String?, videoSource: IVideoSource?, event: IRtcStreamEventHandler?,
            videoCfg: VideoEncoderConfiguration?
    ): IRtcStream {
        val result = rtcChannel.createVideoStream(streamName, videoSource, 1, event, videoCfg)
        Log.e(TAG,"IRtcChannel.createVideoStream(${streamName},source,event) result=${result}")
        return result

    }

    /**
     * 频道内开启推流
     */
    fun publish(publishType: Int = PUBLISH_MEDIA_TYPE.PUBLISH_AUDIO_VIDEO): Int {
        val result = rtcChannel.publish(publishType)
        Log.e(TAG,"IRtcChannel.publish() result=${result}")
        return result
    }

    /**
     * 频道内停止推流
     */
    fun unPublish(publishType: Int=PUBLISH_MEDIA_TYPE.PUBLISH_AUDIO_VIDEO): Int {
        val result = rtcChannel.unPublish(publishType)
        Log.e(TAG,"IRtcChannel.unPublish() result=${result}")
        return result
    }


    /**
     * 初始化远端用户视图。
     *
     * @param canvas 视频画布信息
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setupRemoteVideo(uid: String, streamName: String?, canvas: VideoCanvas): Int {
        val result = rtcChannel.setupRemoteVideo(uid, streamName, canvas)
        Log.e(TAG,"IRtcChannel.setupRemoteVideo(${uid},${streamName},${canvas}) result=${result}")
        return result
    }

    /**
     * 设置是否默认停止接收视频流。
     *
     * @param mute
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setDefaultMuteAllRemoteVideoStreams(mute: Boolean): Int {
        val result = rtcChannel.setDefaultMuteAllRemoteVideoStreams(mute)
        Log.e(TAG,"IRtcChannel.setDefaultMuteAllRemoteVideoStreams(${mute}) result=${result}")
        return result
    }

    /**
     * 接收／停止接收所有远端视频流。
     *
     * @param mute
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun muteAllRemoteVideoStreams(mute: Boolean): Int {
        val result = rtcChannel.muteAllRemoteVideoStreams(mute)
        Log.e(TAG,"IRtcChannel.muteAllRemoteVideoStreams(${mute}) result=${result}")
        return result
    }

    /**
     * 接收／停止接收指定远端用户的视频流
     *
     * @param streamName 流标识
     * @param mute
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun muteRemoteVideoStream(uid: String, streamName: String?, mute: Boolean): Int {
        val result = rtcChannel.muteRemoteVideoStream(uid, streamName, mute)
        Log.e(TAG,"IRtcChannel.muteRemoteVideoStream(${uid},${streamName},${mute}) result=${result}")
        return result
    }

    /**
     * 更新远端视图显示模式。
     *
     * @param streamName 流标识
     * @param renderMode 远端用户视图的渲染模式
     * @param mirrorMode 远端用户视图的镜像模式
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setRemoteRenderMode(
            uid: String, streamName: String?, renderMode: Int, mirrorMode: Int
    ): Int {
        val result = rtcChannel.setRemoteRenderMode(uid, streamName, renderMode, mirrorMode)
        Log.e(TAG,"IRtcChannel.setRemoteRenderMode(${uid},${streamName},${renderMode},${mirrorMode}) result=${result}")
        return result

    }

    /**
     * 设置默认订阅的视频流类型。
     *
     * @param streamType 视频流类型
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setRemoteDefaultVideoStreamType(streamType: Int): Int {
        val result = rtcChannel.setRemoteDefaultVideoStreamType(streamType)
        Log.e(TAG,"IRtcChannel.setRemoteDefaultVideoStreamType(${streamType}) result=${result}")
        return result

    }

    /**
     * 设置订阅的视频流类型。
     *
     * @param streamName 流标识
     * @param streamType 视频流类型
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setRemoteVideoStreamType(uid: String, streamName: String?, streamType: Int): Int {
        val result = rtcChannel.setRemoteVideoStreamType(uid, streamName, streamType)
        Log.e(TAG,"IRtcChannel.setRemoteVideoStreamType(${uid},${streamName},${streamType}) result=${result}")
        return result
    }


    /**
     * 接收／停止接收所有音频流。
     *
     * @param mute true: 停止 false: 继续接收
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun muteAllRemoteAudioStream(mute: Boolean): Int {
        val result = rtcChannel.muteAllRemoteAudioStream(mute)
        Log.e(TAG,"IRtcChannel.muteAllRemoteAudioStream(${mute}) result=${result}")
        return result

    }

    /**
     * 设置是否默认接收所有音频流。
     *
     * @param mute true: 停止 false: 接收
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setDefaultMuteAllRemoteAudioStreams(mute: Boolean): Int {
        val result = rtcChannel.setDefaultMuteAllRemoteAudioStreams(mute)
        Log.e(TAG,"IRtcChannel.setDefaultMuteAllRemoteAudioStreams(${mute}) result=${result}")
        return result
    }

    /**
     * 接收／停止接收指定音频流。
     *
     * @param mute
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun muteRemoteAudioStream(uid: String, mute: Boolean): Int {
        val result = rtcChannel.muteRemoteAudioStream(uid, mute)
        Log.e(TAG,"IRtcChannel.muteRemoteAudioStream(${uid},${mute}) result=${result}")
        return result
    }

    /**
     * 查询网络连接状态
     *
     * @return
     */
    fun getConnectionState(): Int {
        return rtcChannel.getConnectionState()
    }

    fun release() {
        val result =rtcChannel.release()
        Log.e(TAG,"IRtcChannel.release() result=${result}")
        return result

    }
}