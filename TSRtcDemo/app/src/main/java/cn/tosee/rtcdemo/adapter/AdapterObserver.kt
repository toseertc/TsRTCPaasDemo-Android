package cn.tosee.rtc.test.inChannel.adapter

import android.view.View

interface AdapterObserver {
    fun update(position: Int, itemView: View)

    fun updateState(videoData: VideoData)
}