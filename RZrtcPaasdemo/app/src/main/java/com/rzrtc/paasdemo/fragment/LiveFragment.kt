package com.rzrtc.paasdemo.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.rz.paas.IRtcChannel
import com.rz.paas.stats.RtcStats
import com.rz.paas.test.inChannel.adapter.ItemIconClickListener
import com.rz.paas.test.inChannel.adapter.VideoData
import com.rz.paas.test.inChannel.adapter.VideoListAdapter
import com.rz.paas.test.pass_sdk.middle.MiddleRtcChannelEventHandler
import com.rz.paas.video.GLFrameSurfaceView
import com.rz.paas.video.bean.VideoCanvas
import com.rzrtc.paasdemo.*
import com.rzrtc.paasdemo.bean.DataBean
import com.rzrtc.paasdemo.const.*
import com.rzrtc.paasdemo.listener.InformationInterface
import kotlinx.android.synthetic.main.layout_video_fragment.*


class LiveFragment : Fragment() {
    lateinit var dialog: TipsDialog
    private lateinit var videoAdapter: VideoListAdapter
    lateinit var mActivity: MainActivity
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_video_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as MainActivity
        PaaSInstance.addChannelEventHandler(channelEventHandler)
        PaaSInstanceHelper.addListener(informationInterface)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* enabled by default */) {
                override fun handleOnBackPressed() {
                    leave(requireView())
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        exit_button.setOnClickListener {
            leave(it)
        }
        val chanelId = SharePreUtils.getInstance(context).getValue(AppConstant.CHANNEL_ID, "")
        tv_channel.text = "频道号:$chanelId"
        videoAdapter = VideoListAdapter(requireContext())
        video_rv.layoutManager = GridLayoutManager(context, 2)
        video_rv.itemAnimator = null
        video_rv.adapter = videoAdapter
        video_rv.isMotionEventSplittingEnabled = false
        videoAdapter.setItemIconClickListener(clickListener)
        for (dataBean in mActivity.modifyList) {
            addVideoStream(dataBean.uid, dataBean.streamName!!)
            videoAdapter.userMicIconStateChanged(DataBean(dataBean.uid, audioState = 2))
            videoAdapter.userIconStateChanged(DataBean(dataBean.uid, videoState = 2))
        }
    }

    fun leave(view: View) {
        val result = mActivity.leaveChannel()
        if (result != 0 || result == null) {
            mActivity.createChannel?.release()
            Navigation.findNavController(view).navigateUp()
        }
    }

    val clickListener = object : ItemIconClickListener {
        override fun muteVideo(videoData: VideoData, position: Int): Boolean {
//
            mActivity?.createChannel?.muteRemoteVideoStream(
                videoData.userId!!,
                videoData.deviceName!!,
                !videoData.muteCamera
            )


            return false
        }

        override fun muteAudio(videoData: VideoData, position: Int): Boolean {

            mActivity?.createChannel?.muteRemoteAudioStream(
                videoData.userId!!,
                !videoData.muteMicrophone
            )


            return false
        }

        override fun changeVideoSize(videoData: VideoData, position: Int) {
            videoData.isHd = !videoData.isHd

            mActivity?.createChannel?.setRemoteVideoStreamType(
                videoData.userId!!,
                videoData.deviceName!!,
                if (videoData.isHd) REMOTE_VIDEO_STREAM_TYPE.REMOTE_VIDEO_STREAM_HIGH
                else REMOTE_VIDEO_STREAM_TYPE.REMOTE_VIDEO_STREAM_LOW
            )
        }

        override fun switchCamera(videoData: VideoData, position: Int) {

            PaaSInstance.switchCamera()
        }

    }

    fun openDialog(title: String, msg: String) {
        dialog = TipsDialog(requireContext())
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setYesOnclickListener("返回登录页面") {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
            leave(requireView())

        }
        dialog.show()
        dialog.showCloseButton()
    }

    private fun addVideoStream(userID: String, streamName: String) {
        if (!isAdded) {
            return;
        }
        var finded: DataBean? = null
        for (index in mActivity.modifyList.indices) {
            if (mActivity.modifyList[index].uid == userID) {
                finded = mActivity.modifyList[index]
                break
            }
        }
        if (finded == null) {
            return
        }
        finded.streamName = streamName
        finded.videoState = SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_ONLINE
        addData2Adapter(finded)
    }

    private fun generateTextView(res: Int): TextView {

        val drawable = ResourcesCompat.getDrawable(resources, res, requireContext().theme)!!
        drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)

        var imageView = TextView(context)
        imageView.setCompoundDrawables(drawable, null, null, null)
        imageView.setCompoundDrawablePadding(6)
        imageView.setBackgroundResource(R.drawable.video_view_icon_bg)
        imageView.setTextColor(Color.WHITE)
        imageView.textSize = sp2px(3)
        return imageView
    }

    private fun generateImageView(res: Int): ImageView {
        var imageView = ImageView(context)
        imageView.setImageResource(res)
        imageView.setBackgroundResource(R.drawable.video_view_icon_bg)
        return imageView
    }

    private fun addData2Adapter(dataBean: DataBean) {
        var videoData = VideoData()
        val createRenderView = PaaSInstance.createRenderView(requireContext().applicationContext)
        videoData.renderView = createRenderView

        videoData.isSelf = dataBean.isSelf
        videoData.userId = dataBean.uid
        videoData.deviceName = dataBean.streamName
        videoData.channelId = dataBean.channelId
        videoData.camera_operator_iv = generateTextView(R.mipmap.camera_close_icon)
        videoData.mic_volume_iv = generateTextView(R.mipmap.mic_open_icon)
        videoData.mic_operator_iv = generateTextView(R.mipmap.mic_open_icon)
        videoData.switch_camera_iv = generateTextView(R.mipmap.switch_camera_icon)
        videoData.hd_operator_iv = generateTextView(R.mipmap.hd_open_icon)
        videoData.isInit = true
        if (videoData.isSelf) {
            videoAdapter.addFirst(videoData)
        } else {
            videoAdapter.addData(videoData)
            videoData.closeMicrophone = true
        }
        addData(videoData)
    }

    private fun removeList(dataBean: DataBean) {

        for (videoData in videoAdapter.data) {
            if (dataBean.uid == videoData.userId) {
                videoData.renderView = null
                videoAdapter.removeData(videoData)
            }
        }
    }

    private fun addData(it: VideoData) {

        val videoCanvas = VideoCanvas(it.renderView, RENDER_MODE_TYPE.RENDER_MODE_FIT, VIDEO_MIRROR_MODE_TYPE.VIDEO_MIRROR_MODE_AUTO)

        if (it.isSelf) {
            PaaSInstance.setupLocalVideo(videoCanvas)
            PaaSInstance.enableLocalVideo(true)
            PaaSInstance.enableLocalAudio(true)
        } else {
            mActivity.createChannel?.setupRemoteVideo(it.userId!!, it.deviceName, videoCanvas)
        }
    }


    val channelEventHandler = object : MiddleRtcChannelEventHandler() {

        override fun onUserJoined(channel: IRtcChannel, userId: String, elapsed: Int) {
            mActivity.runOnUiThread {
                addVideoStream(userId, "")
            }
        }

        override fun onUserOffline(channel: IRtcChannel, userID: String, reason: Int) {

            mActivity.runOnUiThread {
                for (dataBean in mActivity.modifyList) {
                    if (dataBean.uid == userID) {
                        removeList(dataBean)
                        mActivity.modifyList.remove(dataBean)
                    }
                }
            }
        }

        override fun onLeaveChannel(channel: IRtcChannel, stats: RtcStats) {
            mActivity.runOnUiThread {
                mActivity.createChannel?.release()
                if (isAdded) {
                    Navigation.findNavController(requireView()).navigateUp()
                }
            }
        }

        override fun onAudioSubscribeStateChanged(userID: String, state: Int, elapsed: Int) {
            mActivity.runOnUiThread {
                videoAdapter.userMicIconStateChanged(DataBean(userID, audioState = state))
            }
        }

        override fun onVideoSubscribeStateChanged(
            userId: String, streamName: String, state: Int,
            elapsed: Int
        ) {
            mActivity.runOnUiThread {
                videoAdapter.userIconStateChanged(
                    DataBean(
                        userId,
                        streamName = streamName,
                        videoState = state
                    )
                )
            }
        }

        override fun onConnectionLost(channel: IRtcChannel?) {
            mActivity.runOnUiThread {
                openDialog("已断开连接", "网络链接丢失")
            }
        }
        override fun onError(channel: IRtcChannel?, err: Int, msg: String?) {
            when (err) {
                ERROR_TYPE.ERR_JOIN_CHANNEL_REJECTED,
                ERROR_TYPE.ERR_LEAVE_CHANNEL_REJECTED -> {
                    mActivity.createChannel?.release()
                    mActivity.runOnUiThread {
                        if (isAdded) {
                            Navigation.findNavController(requireView()).navigateUp()
                        }
                    }
                }
            }
        }

        override fun onConnectionStateChanged(channel: IRtcChannel?, state: Int, reason: Int) {
            mActivity.runOnUiThread {
                if (!isAdded) {
                    return@runOnUiThread
                }
                if (state == CONNECTION_STATE_TYPE.CONNECTION_STATE_CONNECTING) {
                    //建立连接中
                }
                if (state == CONNECTION_STATE_TYPE.CONNECTION_STATE_RECONNECTING) {
                    ////网络重连中
                }
                if (reason == CONNECTION_CHANGED_REASON_TYPE.CONNECTION_CHANGED_REJECTED_BY_SERVER) {
                    //被踢
                    openDialog(
                        "已断开连接", "检测到你在其他设备登录\n" +
                                "请返回登录后重试。"
                    )
                }
            }

        }

    }

    var informationInterface = object : InformationInterface() {

        override fun onLocalVideoStateChanged(state: Int, error: Int) {

            mActivity.runOnUiThread {
                if (error == LOCAL_VIDEO_STREAM_ERROR.LOCAL_VIDEO_STREAM_ERROR_DEVICE_NO_PERMISSION) {
                    //没有权限
                    openDialog("提示","需要摄像头麦克风权限")

                }
            }
        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            mActivity. runOnUiThread {
                if (error == LOCAL_AUDIO_STREAM_ERROR.LOCAL_AUDIO_STREAM_ERROR_DEVICE_NO_PERMISSION) {
                    //没有权限
                    openDialog("提示","需要摄像头麦克风权限")
                }
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mActivity.modifyList.clear()
        PaaSInstance.removeChannelEventHandler(channelEventHandler)
        PaaSInstanceHelper.removeListener(informationInterface)
    }
}