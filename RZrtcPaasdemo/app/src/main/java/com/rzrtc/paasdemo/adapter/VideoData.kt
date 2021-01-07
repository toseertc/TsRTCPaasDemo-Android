package com.rz.paas.test.inChannel.adapter

import android.view.SurfaceView
import android.widget.ImageView
import android.widget.TextView

class VideoData {
    var isTopping = false

    //    var imageRes: Int = 0
    var renderView: SurfaceView? = null
    var camera_operator_iv: TextView? = null
    var mic_volume_iv: TextView? = null
    var mic_operator_iv: TextView? = null
    var switch_camera_iv: TextView? = null
    var topping_operator_iv: ImageView? = null
    var hd_operator_iv: TextView? = null

    var userId: String? = null
    var deviceName: String? = null
    var channelId: String? = null

    var isSelf: Boolean = false
    var doubleStream: Boolean = false

    @Volatile
    var muteCamera: Boolean = false

    @Volatile
    var muteMicrophone: Boolean = false

    @Volatile
    var soundVolume: String = "N/A"

    @Volatile
    var closeCamera: Boolean = false

    @Volatile
    var closeMicrophone: Boolean = false
    var isLoading: Boolean = false
    var isInit:Boolean=false;

    var isHd: Boolean = true

    var isFontCamera = true

    val NO_VOLUME = "N/A"


}