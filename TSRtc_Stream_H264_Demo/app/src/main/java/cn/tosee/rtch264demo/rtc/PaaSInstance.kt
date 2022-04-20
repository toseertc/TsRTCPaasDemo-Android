package cn.tosee.rtch264demo.rtc

import android.content.Context
import android.text.TextUtils
import android.util.Log
import android.view.SurfaceView
import cn.tosee.rtc.*
import cn.tosee.rtc.annotation.CalledByNative
import cn.tosee.rtc.internal.LastmileProbeConfig
import cn.tosee.rtc.log.TSLog
import cn.tosee.rtc.stats.*
import cn.tosee.rtc.video.IVideoSink
import cn.tosee.rtc.video.VideoEncoderConfiguration
import cn.tosee.rtc.video.bean.VideoCanvas
import cn.tosee.rtch264demo.rtc.listener.MiddleEngineCallback
import cn.tosee.rtch264demo.rtc.listener.MiddleRtcChannel
import cn.tosee.rtch264demo.rtc.listener.MiddleRtcChannelEventHandler


object PaaSInstance {
    private final val TAG = "PaaSInstance"
    lateinit var mRtcEngine: RtcEngine
    lateinit var mMiddleEngineCallback: MiddleEngineCallback
    var mChannelEventHandlerList = mutableListOf<MiddleRtcChannelEventHandler>()

    fun init(
        context: Context,
        appId: String,
        privateServiceUrl: String,
        codecPriority: Int,
        reInit: Boolean,
        middleCallback: MiddleEngineCallback
    ) {
        mMiddleEngineCallback = middleCallback
        if (reInit) {
            RtcEngine.release(false)
        }
        var rtcEngineContext = RtcEngineContext(context, appId, handler)
        rtcEngineContext.codecPriority = codecPriority
        if (!TextUtils.isEmpty(privateServiceUrl)) {
            rtcEngineContext.envConfig = privateServiceUrl

        }
        Log.e(TAG, "init: ")
        mRtcEngine = RtcEngine.create(rtcEngineContext)
        Log.e(TAG, "RtcEngine.create(${rtcEngineContext.toString()})")
        val interval = 500
        val smooth = 10
        val report_vad = true

        mRtcEngine.enableAudioVolumeIndication(interval, smooth, report_vad)
        Log.e(TAG, "mRtcEngine.enableAudioVolumeIndication(${interval},${smooth},${report_vad})")
    }


    fun destroy() {
        RtcEngine.release(true)
        Log.e(TAG, "RtcEngine.release(${true})")
    }

    fun createRenderView(context: Context): SurfaceView {
        Log.e(TAG, "RtcEngine.createRenderView(${context})")
        return RtcEngine.createRenderView(context/*mContext*/)
    }

    fun addChannelEventHandler(channelEventHandler: MiddleRtcChannelEventHandler) {
        mChannelEventHandlerList.add(channelEventHandler)

    }

    fun removeChannelEventHandler(channelEventHandler: MiddleRtcChannelEventHandler) {
        mChannelEventHandlerList.remove(channelEventHandler)

    }

    fun createChannel(
        profile: Int,
        channelId: String
    ): MiddleRtcChannel {
        val rtcChannel = mRtcEngine.createChannel(profile, channelId, channelEventHandler)
        Log.e(TAG, "mRtcEngine.createChannel(${profile}),${channelId},$channelEventHandler")
        return MiddleRtcChannel(rtcChannel)
    }

    /**
     * 开始通话前网络质量探测。
     */
    fun startLastmileProbeTest(): Int {
        val config = LastmileProbeConfig(true, true, 1024000, 1024000)
        val result = mRtcEngine.startLastmileProbeTest(config)
        Log.e(TAG, "mRtcEngine.startLastmileProbeTest(${config}) return=${result}")
        return result
    }

    /**
     * 停止通话前网络质量探测。
     */
    fun stopLastmileProbeTest(): Int {
        val result = mRtcEngine.stopLastmileProbeTest()
        Log.e(TAG, "mRtcEngine.stopLastmileProbeTest() return=${result}")
        return result

    }

    /**
     * 开关本地摄像头
     */
    fun enableLocalVideo(enabled: Boolean): Int {
        val result = mRtcEngine.enableLocalVideo(enabled)
        Log.e(TAG, "mRtcEngine.enableLocalVideo(${enabled}) return=${result}")
        return result

    }

    /**
     * 设置本地渲染视图
     */
    fun setupLocalVideo(canvas: VideoCanvas): Int {
        val result = mRtcEngine.setupLocalVideo(canvas)
        Log.e(TAG, "mRtcEngine.setupLocalVideo(${canvas}) return=${result}")
        return result
    }


    /**
     * 开启本地预览
     */
    fun startPreview(): Int {
        val result = mRtcEngine.startPreview()
        Log.e(TAG, "mRtcEngine.startPreview() return=${result}")
        return result
    }

    /**
     * 关闭本地预览
     */
    fun stopPreview(): Int {
        val result = mRtcEngine.stopPreview()
        Log.e(TAG, "mRtcEngine.stopPreview() return=${result}")
        return result
    }

    /**
     * 视频编码相关配置
     */
    fun setVideoEncoderConfiguration(configuration: VideoEncoderConfiguration): Int {
        val result = mRtcEngine.setVideoEncoderConfiguration(configuration)
        Log.e(TAG, "mRtcEngine.setVideoEncoderConfiguration(${configuration}) return=${result}")
        return result
    }

    /**
     * 关闭本地视频推送
     */
    fun muteLocalVideoStream(mute: Boolean): Int {
        val result = mRtcEngine.muteLocalVideoStream(mute)
        Log.e(TAG, "mRtcEngine.muteLocalVideoStream(${mute}) return=${result}")
        return result
    }

    /**
     * 是否开启大小流
     */
    fun enableDualStreamMode(enabled: Boolean): Int {
        val result = mRtcEngine.enableDualStreamMode(enabled)
        Log.e(TAG, "mRtcEngine.enableDualStreamMode(${enabled}) return=${result}")
        return result
    }

    /**
     * 设置本地视图显示样式
     */
    fun setLocalRenderMode(renderMode: Int, mirrorMode: Int): Int {
        val result = mRtcEngine.setLocalRenderMode(renderMode, mirrorMode)
        Log.e(TAG, "mRtcEngine.setLocalRenderMode(${renderMode},${mirrorMode}) return=${result}")
        return result

    }

    /**
     * 切换前置/后置摄像头
     *
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun switchCamera(): Int {
        val result = mRtcEngine.switchCamera()
        Log.e(TAG, "mRtcEngine.switchCamera() return=${result}")
        return result
    }

    /**
     * 开关本地音频采集。
     *
     * @param enabled true: 重新开启本地语音功能，即开启本地语音采集（默认）；
     * false: 关闭本地语音功能，即停止本地语音采集。
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun enableLocalAudio(enabled: Boolean): Int {
        val result = mRtcEngine.enableLocalAudio(enabled)
        Log.e(TAG, "mRtcEngine.enableLocalAudio(${enabled}) return=${result}")
        return result

    }

    /**
     * 设置音频编码属性。
     *
     * @param profile  设置采样率，码率，编码模式和声道数:
     * @param scenario 设置音频应用场景
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setAudioProfile(profile: Int, scenario: Int): Int {
        val result = mRtcEngine.setAudioProfile(profile, scenario)
        Log.e(TAG, "mRtcEngine.setAudioProfile(${profile},${scenario}) return=${result}")
        return result
    }


    /**
     * 设置动态码率。
     *
     * @param enablePrediction   是否开启码率预测功能  false: 不开启 true: 开启
     * @param enableAutoAdjust    是否使用自动调整功能 false: 自定义 true: 全自动
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun enableBitratePrediction(enablePrediction: Boolean,  enableAutoAdjust: Boolean): Int {
        val result = mRtcEngine.enableBitratePrediction(enablePrediction,enableAutoAdjust)
        return result
    }


    /**
     * 开关本地音频发送。
     *
     * @param mute true: 停止 false: 继续发送
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun muteLocalAudioStream(mute: Boolean): Int {
        val result = mRtcEngine.muteLocalAudioStream(mute)
        Log.e(TAG, "mRtcEngine.muteLocalAudioStream(${mute}) return=${result}")
        return result
    }

    /**
     * 启用说话者音量提示。
     *
     * @param interval   指定音量提示的时间间隔：
     * @param smooth     平滑系数，指定音量提示的灵敏度。
     * @param report_vad 开启本地人声检测功能。
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun enableAudioVolumeIndication(interval: Int, smooth: Int, report_vad: Boolean): Int {
        val result = mRtcEngine.enableAudioVolumeIndication(interval, smooth, report_vad)
//        Log.e(TAG,"mRtcEngine.enableAudioVolumeIndication(${interval},${smooth},${report_vad}) return=${result}")
        return result

    }

    /**
     * 设置默认的语音路由
     *
     * @param defaultToSpeaker
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setDefaultAudioRouteToSpeakerphone(defaultToSpeaker: Boolean): Int {
        val result = mRtcEngine.setDefaultAudioRouteToSpeakerphone(defaultToSpeaker)
        Log.e(
            TAG,
            "mRtcEngine.setDefaultAudioRouteToSpeakerphone(${defaultToSpeaker}) return=${result}"
        )
        return result

    }

    /**
     * 启用/关闭扬声器播放
     *
     * @param speakerOn
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setEnableSpeakerphone(speakerOn: Boolean): Int {
        val result = mRtcEngine.setEnableSpeakerphone(speakerOn)
        Log.e(TAG, "mRtcEngine.setEnableSpeakerphone(${speakerOn}) return=${result}")
        return result
    }

    /**
     * 查询扬声器启用状态
     *
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun isSpeakerphoneEnabled(): Boolean {
        val result = mRtcEngine.isSpeakerphoneEnabled()
        Log.e(TAG, "mRtcEngine.isSpeakerphoneEnabled() return=${result}")
        return result
    }

    /**
     * 设置 SDK 输出的日志文件。
     *
     * @param filePath
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setLogFile(filePath: String): Int {
        return mRtcEngine.setLogFile(filePath)
    }

    /**
     * 设置日志输出等级
     *
     * @param filter
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setLogFilter(filter: Int): Int {
        return mRtcEngine.setLogFilter(filter)
    }

    /**
     * 设置 SDK 输出的单个日志文件大小。
     *
     * @param fileSizeInKBytes
     * @return 0: 方法调用成功
     * < 0: 方法调用失败
     */
    fun setLogFileSize(fileSizeInKBytes: Int): Int {
        return mRtcEngine.setLogFileSize(fileSizeInKBytes)
    }

    /**
     * 获取警告或错误描述。
     */
    fun getErrorDescription(code: Int): String {
        return mRtcEngine.getErrorDescription(code)
    }

    /**
     * 查询 SDK 版本号。
     *
     * @return
     */
    fun getVersion(): String {
        return RtcEngine.getVersion()
    }

    fun setLocalVideoSink(videoSink: IVideoSink): Int {
        return mRtcEngine.setLocalVideoSink(videoSink)

    }

    val handler = object : IRtcEngineEventHandler() {

        @CalledByNative
        override fun onWarning(warn: Int, msg: String) {
            Log.e(TAG, "IRtcEngineEventHandler.onWarning() warn=${warn},msg=${msg}")
            mMiddleEngineCallback.onWarning(warn, msg)
        }

        @CalledByNative
        override fun onError(err: Int, msg: String) {
            Log.e(TAG, "IRtcEngineEventHandler.onError() err=${err},msg=${msg}")
            mMiddleEngineCallback.onError(err, msg)
        }

        @CalledByNative
        override fun onLocalVideoStateChanged(state: Int, error: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onLocalVideoStateChanged() state=${state},err=${error}"
            )
            mMiddleEngineCallback.onLocalVideoStateChanged(state, error)
        }

        @CalledByNative
        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onLocalAudioStateChanged() state=${state},err=${error}"
            )
            mMiddleEngineCallback.onLocalAudioStateChanged(state, error)
        }

        override fun onAudioVolumeIndication(speakers: Array<AudioVolumeInfo>?, totalVolume: Int) {
            super.onAudioVolumeIndication(speakers, totalVolume)
            mMiddleEngineCallback.onAudioVolumeIndication(speakers, totalVolume)
        }

        @CalledByNative
        override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onFirstLocalVideoFrame() width=${width},height=${height},elapsed=${elapsed}"
            )
            mMiddleEngineCallback.onFirstLocalVideoFrame(width, height, elapsed)
        }

        @CalledByNative
        override fun onFirstLocalVideoFramePublished(elapsed: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onFirstLocalVideoFramePublished() elapsed=${elapsed}"
            )
            mMiddleEngineCallback.onFirstLocalVideoFramePublished(elapsed)
        }

        @CalledByNative
        override fun onMediaDeviceStateChanged(
            deviceId: String,
            deviceType: Int,
            deviceState: Int
        ) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onMediaDeviceStateChanged() deviceId=${deviceId},deviceType=${deviceType},deviceState=${deviceState}"
            )
            mMiddleEngineCallback.onMediaDeviceStateChanged(deviceId, deviceType, deviceState)
        }

        @CalledByNative
        override fun onAudioDeviceVolumeChanged(deviceType: Int, volume: Int, muted: Boolean) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onAudioDeviceVolumeChanged() deviceType=${deviceType},volume=${volume},muted=${muted}"
            )
            mMiddleEngineCallback.onAudioDeviceVolumeChanged(deviceType, volume, muted)
        }

        @CalledByNative
        override fun onVideoSizeChanged(width: Int, height: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onVideoSizeChanged() width=${width},height=${height}"
            )
            mMiddleEngineCallback.onVideoSizeChanged(width, height)
        }

        @CalledByNative
        override fun onFirstLocalAudioFramePublished(elapsed: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onFirstLocalAudioFramePublished() elapsed=${elapsed}"
            )
            mMiddleEngineCallback.onFirstLocalAudioFramePublished(elapsed)
        }

        @CalledByNative
        override fun onAudioRouteChanged(routing: Int) {
            Log.e(TAG, "IRtcEngineEventHandler.onAudioRouteChanged() routing=${routing}")
            mMiddleEngineCallback.onAudioRouteChanged(routing)
        }


        @CalledByNative
        override fun onNetworkTypeChanged(type: Int) {
            Log.e(TAG, "IRtcEngineEventHandler.onNetworkTypeChanged() type=${type}")
            mMiddleEngineCallback.onNetworkTypeChanged(type)
        }


        @CalledByNative
        override fun onLocalAudioStats(stats: LocalAudioStats) {
            mMiddleEngineCallback.onLocalAudioStats(stats)
        }


        @CalledByNative
        override fun onLocalVideoStats(stats: LocalVideoStats) {
            mMiddleEngineCallback.onLocalVideoStats(stats)
        }

        override fun onLocalNetworkQuality(txQuality: Int, rxQuality: Int) {
//            Log.e(TAG,"IRtcEngineEventHandler.onLocalNetworkQuality() txQuality=${txQuality},rxQuality=${rxQuality}")
            mMiddleEngineCallback.onLocalNetworkQuality(txQuality, rxQuality)
        }

        @CalledByNative
        override fun onLastmileProbeResult(result: LastmileProbeResult) {
            Log.e(TAG, "IRtcEngineEventHandler.onLastmileProbeResult() result=${result}")
            mMiddleEngineCallback.onLastmileProbeResult(result)
        }
    }

    val channelEventHandler = object : IRtcChannelEventHandler() {
        @CalledByNative
        override fun onJoinChannelSuccess(channel: IRtcChannel, userID: String, elapsed: Int) {
            Log.e(
                TAG,
                "IRtcChannelEventHandler.onJoinChannelSuccess() userID=${userID},elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onJoinChannelSuccess(channel, userID, elapsed)
            }
        }

        @CalledByNative
        override fun onRejoinChannelSuccess(channel: IRtcChannel, userID: String, elapsed: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onRejoinChannelSuccess() userID=${userID},elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onRejoinChannelSuccess(channel, userID, elapsed)
            }
        }

        @CalledByNative
        override fun onLeaveChannel(channel: IRtcChannel, stats: RtcStats) {
            Log.e(TAG, "IRtcEngineEventHandler.onLeaveChannel() stats=${stats}")
            for (listener in mChannelEventHandlerList) {
                listener.onLeaveChannel(channel, stats)
            }
        }

        @CalledByNative
        override fun onClientRoleChanged(
            channel: IRtcChannel,
            oldRole: Int,
            newRole: Int,
            elapsed: Int
        ) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onClientRoleChanged() oldRole=${oldRole},newRole=${newRole},elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onClientRoleChanged(channel, oldRole, newRole, elapsed)
            }
        }

        @CalledByNative
        override fun onUserJoined(channel: IRtcChannel, userID: String, elapsed: Int) {
            Log.e(TAG, "IRtcEngineEventHandler.onUserJoined() userID=${userID},elapsed=${elapsed}")
            for (listener in mChannelEventHandlerList) {
                listener.onUserJoined(channel, userID, elapsed)
            }
        }

        @CalledByNative
        override fun onUserOffline(channel: IRtcChannel, userID: String, reason: Int) {
            Log.e(TAG, "IRtcEngineEventHandler.onUserOffline() userID=${userID},reason=${reason}")
            for (listener in mChannelEventHandlerList) {
                listener.onUserOffline(channel, userID, reason)
            }
        }

        @CalledByNative
        override fun onAudioPublishStateChanged(channel: IRtcChannel, state: Int, elapsed: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onAudioPublishStateChanged() state=${state},elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onAudioPublishStateChanged(state, elapsed)
            }
        }

        @CalledByNative
        override fun onVideoPublishStateChanged(channel: IRtcChannel, state: Int, elapsed: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onVideoPublishStateChanged() state=${state},elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onVideoPublishStateChanged(state, elapsed)
            }
        }

        @CalledByNative
        override fun onAudioSubscribeStateChanged(
            channel: IRtcChannel,
            userID: String,
            state: Int,
            elapsed: Int
        ) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onAudioSubscribeStateChanged() userID=${userID} state=${state},elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onAudioSubscribeStateChanged(userID, state, elapsed)
            }
        }

        @CalledByNative
        override fun onVideoSubscribeStateChanged(
            channel: IRtcChannel,
            userID: String, streamName: String, state: Int, elapsed: Int
        ) {
            Log.e(
                TAG, "IRtcEngineEventHandler.onVideoSubscribeStateChanged() userID=${userID} " +
                        "streamName=${streamName} state=${state},elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onVideoSubscribeStateChanged(userID, streamName, state, elapsed)
            }
        }

        override fun onFirstRemoteVideoFrame(
            channel: IRtcChannel, userID: String,
            streamName: String, width: Int, height: Int, elapsed: Int
        ) {
            super.onFirstRemoteVideoFrame(channel, userID, streamName, width, height, elapsed)
            for (listener in mChannelEventHandlerList) {
                listener.onFirstRemoteVideoFrame(userID, streamName, width, height, elapsed)
            }
            Log.e(
                TAG, "IRtcEngineEventHandler.onFirstRemoteVideoFrame() userID=${userID}" +
                        " streamName=${streamName} width=${width} height=${height} elapsed=${elapsed}"
            )
        }

        @CalledByNative
        override fun onVideoSizeChanged(
            channel: IRtcChannel,
            userID: String, streamName: String, width: Int, height: Int, elapsed: Int
        ) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onVideoSizeChanged() userID=${userID} streamName=${streamName}" +
                        ",width=${width} height=${height} elapsed=${elapsed}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onVideoSizeChanged(userID, streamName, width, height, elapsed)
            }
        }

        override fun onWarning(channel: IRtcChannel?, warn: Int, msg: String?) {
            Log.e(TAG, "IRtcEngineEventHandler.onWarning() warn=${warn} msg=${msg}")
            for (listener in mChannelEventHandlerList) {
                listener.onWarning(channel, warn, msg)
            }
        }

        override fun onError(channel: IRtcChannel?, err: Int, msg: String?) {
            TSLog.e("IRtcEngineEventHandler.onError() err=${err} msg=${msg}")
            Log.e(TAG, "IRtcEngineEventHandler.onError() err=${err} msg=${msg}")
            for (listener in mChannelEventHandlerList) {
                listener.onError(channel, err, msg)
            }
        }

        override fun onNetworkQuality(
            channel: IRtcChannel?,
            userID: String?,
            txQuality: Int,
            rxQuality: Int
        ) {
            for (listener in mChannelEventHandlerList) {
                listener.onNetworkQuality(channel, userID, txQuality, rxQuality)
            }
        }

        override fun onConnectionLost(channel: IRtcChannel?) {
            Log.e(TAG, "IRtcEngineEventHandler.onConnectionLost()")
            for (listener in mChannelEventHandlerList) {
                listener.onConnectionLost(channel)
            }
        }

        override fun onConnectionStateChanged(channel: IRtcChannel?, state: Int, reason: Int) {
            Log.e(
                TAG,
                "IRtcEngineEventHandler.onConnectionStateChanged(),state=${state},reason=${reason}"
            )
            for (listener in mChannelEventHandlerList) {
                listener.onConnectionStateChanged(channel, state, reason)
            }
        }

        override fun onRemoteAudioStats(channel: IRtcChannel, stats: RemoteAudioStats) {
            for (listener in mChannelEventHandlerList) {
                listener.onRemoteAudioStats(channel, stats)
            }
        }

        override fun onRemoteVideoStats(channel: IRtcChannel, stats: RemoteVideoStats) {
            for (listener in mChannelEventHandlerList) {
                listener.onRemoteVideoStats(channel, stats)
            }
        }

        override fun onRtcStats(channel: IRtcChannel, stats: RtcStats) {
            for (listener in mChannelEventHandlerList) {
                listener.onRtcStats(channel, stats)
            }
        }

        override fun onPredictedBitrateChanged(
            channel: IRtcChannel?,
            uid: String?,
            streamName: String?,
            newBitrate: Int,
            isLowVideo: Boolean
        ) {
            for (listener in mChannelEventHandlerList) {
                listener.onPredictedBitrateChanged(uid, streamName, newBitrate, isLowVideo)
            }
        }
    }


}