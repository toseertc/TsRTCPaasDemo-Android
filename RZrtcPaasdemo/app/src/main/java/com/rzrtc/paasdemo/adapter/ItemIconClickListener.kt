package com.rz.paas.test.inChannel.adapter

interface ItemIconClickListener {
    fun muteVideo(videoData: VideoData, position: Int):Boolean
    fun muteAudio(videoData: VideoData, position: Int):Boolean
    fun changeVideoSize(videoData: VideoData, position: Int)
    fun switchCamera(videoData: VideoData, position: Int)

}