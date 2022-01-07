package cn.tosee.rtch264demo.bean

import android.view.SurfaceView
import cn.tosee.rtc.video.IVideoSink

class DataBean(
        var uid: String = "",
        var streamName: String? = "",
        val isSelf: Boolean = false,
        var surfaceview: SurfaceView? = null,
        var width: Int = 640,
        var height: Int = 360,

){
        lateinit var videoSink: IVideoSink

        override fun toString(): String {
                return "DataBean(uid='$uid', streamName=$streamName, isSelf=$isSelf, surfaceview=$surfaceview, width=$width, height=$height)"
        }
}