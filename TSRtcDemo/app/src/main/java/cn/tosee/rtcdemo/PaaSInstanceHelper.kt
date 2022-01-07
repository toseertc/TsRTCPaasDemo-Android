package cn.tosee.rtcdemo

import android.content.Context
import cn.tosee.rtc.stats.AudioVolumeInfo
import cn.tosee.rtc.stats.LastmileProbeResult
import cn.tosee.rtc.stats.LocalAudioStats
import cn.tosee.rtc.stats.LocalVideoStats
import cn.tosee.rtc.test.pass_sdk.middle.MiddleEngineCallback
import cn.tosee.rtcdemo.const.AppConstant
import cn.tosee.rtcdemo.listener.InformationInterface

object PaaSInstanceHelper {

    fun init(
        applicationContext: Context,
        privateServiceUrl: String = "",
        reInit: Boolean = false,
        codecPriority:Int=0,
        appId: String= AppConstant.DEFAULT_APPID,
    ) {
        PaaSInstance.init(
                applicationContext,
                appId,
                privateServiceUrl,
                codecPriority,
                reInit,
                object : MiddleEngineCallback() {
                    override fun onWarning(warn: Int, msg: String) {
                    }

                    override fun onError(err: Int, msg: String) {
                        for (listener in listeners) {
                            listener.onError(err, msg)
                        }
                    }

                    override fun onLocalVideoStateChanged(state: Int, error: Int) {
                        for (listener in listeners) {
                            listener.onLocalVideoStateChanged(state, error)
                        }
                    }

                    override fun onLocalAudioStateChanged(state: Int, error: Int) {
                        for (listener in listeners) {
                            listener.onLocalAudioStateChanged(state, error)

                        }
                    }

                    override fun onAudioVolumeIndication(
                            speakers: Array<AudioVolumeInfo>?, totalVolume: Int
                    ) {
                        for (listener in listeners) {
                            listener.onAudioVolumeIndication(speakers, totalVolume)

                        }
                    }

                    override fun onFirstLocalVideoFrame(width: Int, height: Int, elapsed: Int) {
                        for (listener in listeners) {
                            listener.onFirstLocalVideoFrame(width, height,elapsed)

                        }
                    }

                    override fun onFirstLocalVideoFramePublished(elapsed: Int) {
                        for (listener in listeners) {
                            listener.onFirstLocalVideoFramePublished(elapsed)

                        }
                    }

                    override fun onMediaDeviceStateChanged(
                            deviceId: String, deviceType: Int, deviceState: Int
                    ) {
                        for (listener in listeners) {
                            listener.onMediaDeviceStateChanged(deviceId,deviceType,deviceState)

                        }
                    }

                    override fun onAudioDeviceVolumeChanged(
                            deviceType: Int,
                            volume: Int,
                            muted: Boolean
                    ) {
                        for (listener in listeners) {
                            listener.onAudioDeviceVolumeChanged(deviceType,volume,muted)

                        }
                    }

                    override fun onVideoSizeChanged(width: Int, height: Int) {
                        for (listener in listeners) {
                            listener.onVideoSizeChanged(width,height)

                        }
                    }

                    override fun onFirstLocalAudioFramePublished(elapsed: Int) {
                        for (listener in listeners) {
                            listener.onFirstLocalAudioFramePublished(elapsed)

                        }
                    }

                    override fun onAudioRouteChanged(routing: Int) {
                        for (listener in listeners) {
                            listener.onAudioRouteChanged(routing)

                        }
                    }


                    override fun onNetworkTypeChanged(type: Int) {
                        for (listener in listeners) {
                            listener.onNetworkTypeChanged(type)

                        }
                    }

                    override fun onLocalAudioStats(stats: LocalAudioStats) {
                        for (listener in listeners) {
                            listener.onLocalAudioStats(stats)

                        }
                    }


                    override fun onLocalVideoStats(stats: LocalVideoStats) {
                        for (listener in listeners) {
                            listener.onLocalVideoStats(stats)

                        }
                    }

                    override fun onLastmileProbeResult(result: LastmileProbeResult) {
                        for (listener in listeners) {
                            listener.onLastmileProbeResult(result)
                        }
                    }

                    override fun onLocalNetworkQuality(txQuality: Int, rxQuality: Int) {
                        for (listener in listeners) {
                            listener.onLocalNetworkQuality(txQuality, rxQuality)
                        }
                    }
                }
        )
    }

    val listeners = mutableListOf<InformationInterface>()

    fun addListener(listener: InformationInterface) {
        listeners.add(listener)
    }

    fun removeListener(listener: InformationInterface) {
        listeners.remove(listener)
    }

    fun release() {
        PaaSInstance.destroy()

    }
}