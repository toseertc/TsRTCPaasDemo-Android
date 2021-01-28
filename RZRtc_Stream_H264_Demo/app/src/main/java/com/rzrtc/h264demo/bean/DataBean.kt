package com.rzrtc.h264demo.bean

import android.view.SurfaceView
import com.rz.paas.video.IVideoSink

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