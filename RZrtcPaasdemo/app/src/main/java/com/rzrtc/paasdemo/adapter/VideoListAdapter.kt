package com.rz.paas.test.inChannel.adapter

import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rzrtc.paasdemo.*
import com.rzrtc.paasdemo.bean.DataBean
import com.rzrtc.paasdemo.const.SUBSCRIBE_STREAM_STATE
import com.rzrtc.paasdemo.recycleview.CommonAdapter
import com.rzrtc.paasdemo.recycleview.CommonHolder


class VideoListAdapter(val context: Context) :
    CommonAdapter<VideoData>(context, R.layout.video_list_item),
    Observable {
    override fun bindData(holder: CommonHolder, data: VideoData, position: Int) {
        val videoViewContainer = holder.findView<FrameLayout>(R.id.video_view_container)
        videoViewContainer.removeAllViews()
        if (videoViewContainer.childCount <= 0) {
            val renderView = data.renderView
            val layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT
            )
            renderView?.layoutParams = layoutParams
            if (renderView?.parent != null && renderView.parent != videoViewContainer) {
                (renderView?.parent as ViewGroup).removeView(renderView)
            }
            if (renderView != null) {
                videoViewContainer.addView(renderView)
            }
        }
        userName(holder, data)
        coverChange(holder, data)
        iconTips(data, holder, position)

    }

    private fun userName(holder: CommonHolder, data: VideoData) {
        var userId = if (data.isSelf) {
            data.userId + " " + data?.deviceName + "(我)"
        } else {
            data.userId!! + " " + data?.deviceName
        }
        holder.setText(R.id.user_id_tv, userId)
    }

    private fun coverChange(holder: CommonHolder, data: VideoData) {
        var findView = holder.findView<FrameLayout>(R.id.close_camera_tips)
        if (!data.isSelf) {
            findView.visibility = View.VISIBLE
            if (data.isInit) {
                holder.setImage(R.id.close_camera_tips_iv, R.mipmap.close_camer_tip_img)
                holder.setText(R.id.close_camera_tips_tv, "远端视频流停止发送")
            } else if (data.isLoading) {
                holder.setImage(R.id.close_camera_tips_iv, R.mipmap.icon_loading)
                holder.setText(R.id.close_camera_tips_tv, "加载中...")
            } else if (data.muteCamera && data.closeCamera) {
                holder.setImage(R.id.close_camera_tips_iv, R.mipmap.close_camer_tip_img)
                holder.setText(R.id.close_camera_tips_tv, "已停止拉取远端视频流\n远端视频流停止发送")
            } else if (data.closeCamera) {
                holder.setImage(R.id.close_camera_tips_iv, R.mipmap.close_camer_tip_img)
                holder.setText(R.id.close_camera_tips_tv, "远端视频流停止发送")
            } else if (data.muteCamera) {
                holder.setImage(R.id.close_camera_tips_iv, R.mipmap.close_camer_tip_img)
                holder.setText(R.id.close_camera_tips_tv, "已停止拉取远端视频流")
            } else {
                findView.visibility = View.INVISIBLE
            }
        } else {
            if (data.closeCamera) {
                holder.setImage(R.id.close_camera_tips_iv, R.mipmap.close_camer_tip_img)
                holder.setText(R.id.close_camera_tips_tv, "已停止推送视频流")
                findView.visibility = View.VISIBLE
            } else {
                findView.visibility = View.INVISIBLE
            }
        }

        if (data.isTopping) {
            holder.findView<ImageView>(R.id.cover_image).visibility = View.VISIBLE
        } else {
            holder.findView<ImageView>(R.id.cover_image).visibility = View.INVISIBLE
        }
    }

    private fun iconTips(videoData: VideoData, holder: CommonHolder, position: Int) {

        holder.findView<LinearLayout>(R.id.icon_container).removeAllViews()
        removeViewByParent(videoData.switch_camera_iv!!)
        removeViewByParent(videoData.camera_operator_iv!!)
        removeViewByParent(videoData.mic_volume_iv!!)

        videoData.camera_operator_iv?.setOnClickListener {
            var muteVideo = itemIconClickListener?.muteVideo(videoData, position)
            if (muteVideo!!) return@setOnClickListener
        }

        videoData.mic_operator_iv?.setOnClickListener {
            var muteAudio = itemIconClickListener?.muteAudio(videoData, position)
            if (muteAudio!!) return@setOnClickListener
            setUserMicroPhoneIcon(videoData, position)

        }
        holder.removeAllView(R.id.title_icon_container)

        if (videoData.isSelf) {
            videoData.switch_camera_iv?.setOnClickListener {
                videoData.isFontCamera = !videoData.isFontCamera
                videoData.switch_camera_iv!!.text = if (videoData.isFontCamera) "前置" else "后置"
                itemIconClickListener?.switchCamera(videoData, position)
            }
            addView(
                holder,
                getCameraSwitchText(videoData.switch_camera_iv!!, videoData),
                videoData,
                R.id.icon_container
            )
            removeView(holder, videoData.hd_operator_iv as View)
            removeView(holder, videoData.mic_operator_iv as View)
            removeView(holder, videoData.camera_operator_iv as View)
        } else {
            addView(
                holder,
                getCameraIcon(videoData.camera_operator_iv!!, videoData),
                videoData,
                R.id.icon_container
            )
            addView(
                holder,
                getMicOperatorIcon(videoData.mic_operator_iv!!, videoData),
                videoData,
                R.id.icon_container
            )
            addView(
                holder,
                getDuaStreamSWitchIcon(videoData.hd_operator_iv!!, videoData),
                videoData,
                R.id.icon_container
            )
            removeView(holder, videoData.switch_camera_iv as View)

            videoData.hd_operator_iv?.setOnClickListener {
                itemIconClickListener?.changeVideoSize(videoData, position)
            }
        }

    }

    private fun setUserMicroPhoneIcon(videoData: VideoData, position: Int) {
        getMicOperatorIcon(videoData.mic_operator_iv!!, videoData)
        findSameUserIdData(position, videoData)
    }

    fun setUserCameraIcon(videoData: VideoData, position: Int) {
        getCameraIcon(videoData.camera_operator_iv!!, videoData)
    }


    fun findSameUserIdData(position: Int, videoData: VideoData) {
        for (index in 0 until data.size) {
            if (data[index].userId == videoData.userId && position != index) {
                data[index].muteMicrophone = videoData.muteMicrophone
                getMicOperatorIcon(data[index].mic_operator_iv!!, data[index])
//                getMicIcon(data[index].mic_volume_iv!!, data[index])
                break
            }
        }
    }

    var selectedPosition = -1
    private fun removeViewByParent(view: View) {
        if (view.parent != null)
            (view.parent as LinearLayout).removeView(view)
    }

    private fun removeView(holder: CommonHolder, childView: View) {
        holder.removeView(R.id.icon_container, childView)
    }

    private val TAG = "VideoListAdapter"
    private fun addView(holder: CommonHolder, childView: View, videoData: VideoData, parent: Int) {
        val miclayoutParams = if (childView is TextView) {
            val width =
                if (childView == videoData.camera_operator_iv || childView == videoData.mic_operator_iv) {
                    50
                } else {
                    40
                }
            LinearLayout.LayoutParams(dp2px(width), dp2px(20))
        } else {
            LinearLayout.LayoutParams(dp2px(20), dp2px(20))
        }
        miclayoutParams.gravity = Gravity.LEFT
        miclayoutParams.setMargins(0, 5, 0, 0)
        childView.layoutParams = miclayoutParams
        holder.addView(parent, childView)
    }

    private fun dp2px(dp: Int): Int {
        val scale = context.resources.displayMetrics.density;
        return (dp * scale + 0.5f).toInt()
    }

    val observers = mutableListOf<AdapterObserver>()

    private fun changeView(position: Int, itemView: View) {
        for (observer in observers) {
            observer.update(position, itemView)
        }
    }

    override fun addObserver(o: AdapterObserver) {
        observers.add(o)
    }

    override fun deleteObservers() {
        observers.clear()

    }

    fun userIconStateChanged(changeBean: DataBean) {
        for (videoData in this.data) {
            if (videoData.userId.equals(changeBean.uid) && videoData.deviceName.equals(changeBean.streamName)) {
                var oldVideoState = videoData.closeCamera
                var oldMuteCameraState = videoData.muteCamera
                val oldLoading = videoData.isLoading
                var shouldUpdate = false
                videoData.isInit = false
                when (changeBean.videoState) {
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_FIRST_FRAME -> {
                        videoData.muteCamera = false
                        videoData.closeCamera = false
                        videoData.isLoading = false
                        shouldUpdate = true
                        Log.e(
                            TAG,
                            "userIconStateChanged: ${changeBean.uid}_${videoData.deviceName} " + changeBean.videoState
                        )
                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_ONLINE -> {
                        videoData.muteCamera = false
                        videoData.closeCamera = false
                        videoData.isLoading = true
                        shouldUpdate = true
                        Log.e(
                            TAG,
                            "userIconStateChanged: ${changeBean.uid}_${videoData.deviceName} " + changeBean.videoState
                        )
                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_NO_SEND -> {
                        videoData.muteCamera = false
                        videoData.closeCamera = true
                        videoData.isLoading = false
                        Log.e(
                            TAG,
                            "userIconStateChanged:${changeBean.uid}_${videoData.deviceName} " + changeBean.videoState
                        )
                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_NO_RECV -> {
                        videoData.muteCamera = true
                        videoData.closeCamera = false
                        videoData.isLoading = false
                        Log.e(
                            TAG,
                            "userIconStateChanged:${changeBean.uid}_${videoData.deviceName} " + changeBean.videoState
                        )
                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_NO_SEND_RECV -> {
                        videoData.muteCamera = true
                        videoData.closeCamera = true
                        videoData.isLoading = false
                        Log.e(
                            TAG,
                            "userIconStateChanged:${changeBean.uid}_${videoData.deviceName} " + changeBean.videoState
                        )
                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_SUBSCRIBING -> {
                        videoData.muteCamera = false
                        videoData.closeCamera = false
                        videoData.isLoading = true
                        shouldUpdate = true
                        Log.e(
                            TAG,
                            "userIconStateChanged:${changeBean.uid}_${videoData.deviceName} " + changeBean.videoState
                        )
                    } //正在订阅
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_SUBSCRIBED -> {
                        videoData.muteCamera = false
                        videoData.closeCamera = false
                        videoData.isLoading = false
                        Log.e(
                            TAG,
                            "userIconStateChanged:${changeBean.uid}_${videoData.deviceName} " + changeBean.videoState
                        )
                    }//订阅成功

                }
                val index = data.indexOf(videoData)
                if (videoData.isTopping) {
                    for (observer in observers) {
                        observer.updateState(videoData)
                    }
                }
                setUserCameraIcon(videoData, index)

                if (shouldUpdate || oldVideoState != videoData.closeCamera || oldMuteCameraState != videoData.muteCamera || oldLoading != videoData.isLoading) {
                    notifyItemChanged(index)
                }

            }
        }
        Log.e(TAG, "userIconStateChanged: after")

    }

    fun userMicIconStateChanged(changeBean: DataBean) {
        for (videoData in this.data) {
            if (videoData.userId.equals(changeBean.uid)) {
                when (changeBean.audioState) {
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_ONLINE -> {
                        videoData.muteMicrophone = false
                        videoData.closeMicrophone = false

                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_NO_SEND -> {
                        videoData.muteMicrophone = false
                        videoData.closeMicrophone = true
                        videoData.soundVolume = videoData.NO_VOLUME
                        videoData.mic_volume_iv?.text = videoData.soundVolume
                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_NO_RECV -> {
                        videoData.muteMicrophone = true
                        videoData.closeMicrophone = false
                        videoData.soundVolume = videoData.NO_VOLUME
                        videoData.mic_volume_iv?.text = videoData.soundVolume
                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_NO_SEND_RECV -> {
                        videoData.muteMicrophone = true
                        videoData.closeMicrophone = true
                        videoData.soundVolume = videoData.NO_VOLUME
                        videoData.mic_volume_iv?.text = videoData.soundVolume

                    }
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_SUBSCRIBING -> {
                        videoData.muteMicrophone = false
                        videoData.closeMicrophone = false
                    } //正在订阅
                    SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_SUBSCRIBED -> {
                        videoData.muteMicrophone = false
                        videoData.closeMicrophone = false
                    }//订阅成功
                }
                val index = data.indexOf(videoData)
                if (videoData.isTopping) {
                    for (observer in observers) {
                        observer.updateState(videoData)
                    }
                }
                setUserMicroPhoneIcon(videoData, index)
            }
        }
    }


    private var itemIconClickListener: ItemIconClickListener? = null
    fun setItemIconClickListener(listener: ItemIconClickListener) {
        itemIconClickListener = listener
    }
}