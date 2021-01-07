package com.rzrtc.paasdemo.bean

import android.view.SurfaceView

class DataBean(
        var uid: String = "",
        val mute: Boolean = false,
        var streamName: String? = "",
        val channelId: String = "",
        val isSelf: Boolean = false,
        var surfaceview: SurfaceView? = null,
        var videoState: Int = -1,
        var audioState: Int = -1,
        var renderMode: Int = 2,
        var mirrorMode: Int = 0,
        var width: Int = 640,
        var height: Int = 360,
        var frameRate: Int = 15,
        var direction: Int = -1,
        var isHd: Boolean = true,
        var txQuality:Int = 0,
        var custompush:Boolean=false

){
    override fun toString(): String {
        return "DataBean(uid='$uid', mute=$mute, streamName='$streamName', channelId='$channelId', isSelf=$isSelf, surfaceview=$surfaceview, videoState=$videoState, audioState=$audioState, renderMode=$renderMode, mirrorMode=$mirrorMode, width=$width, height=$height, frameRate=$frameRate, direction=$direction, isHd=$isHd)"
    }
}