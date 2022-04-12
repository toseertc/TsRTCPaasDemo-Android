package cn.tosee.rtch264demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import cn.tosee.rtc.IRtcChannel
import cn.tosee.rtc.stats.RemoteVideoStats
import cn.tosee.rtc.stats.RtcStats
import cn.tosee.rtc.stream.IRtcStream
import cn.tosee.rtc.video.GLFrameSurfaceView
import cn.tosee.rtch264demo.MainActivity
import cn.tosee.rtch264demo.R
import cn.tosee.rtch264demo.bean.DataBean
import cn.tosee.rtch264demo.constant.*
import cn.tosee.rtch264demo.customvideo.CustomVideoSink
import cn.tosee.rtch264demo.rtc.PaaSInstance
import cn.tosee.rtch264demo.rtc.PaaSInstanceHelper
import cn.tosee.rtch264demo.rtc.listener.InformationInterface
import cn.tosee.rtch264demo.rtc.listener.MiddleRtcChannelEventHandler
import cn.tosee.rtch264demo.rtc.listener.MiddleStreamCallback
import cn.tosee.rtch264demo.utils.BitrateUtil
import cn.tosee.rtch264demo.utils.NetworkUtils
import cn.tosee.rtch264demo.utils.SharePreUtils
import cn.tosee.rtch264demo.view.TipsDialog
import kotlinx.android.synthetic.main.layout_video_fragment.*


class LiveFragment : Fragment() {
    lateinit var dialog: TipsDialog
    lateinit var mActivity: MainActivity
    var kick=false
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
        mActivity.addStreamHandler(rtcStreamCallback)
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
        fl_remote_id.visibility = View.GONE
        for (dataBean in mActivity.modifyList) {
            addStream(dataBean)
        }
    }

    private fun addStream(dataBean: DataBean) {
        if (dataBean.isSelf) {
            fl_local_container.removeAllViews()
            val localView = PaaSInstance.createRenderView(this.requireContext())
            val customVideoSink = CustomVideoSink(false)
            dataBean.videoSink = customVideoSink
            customVideoSink.setSurfaceView(localView as GLFrameSurfaceView)
            mActivity.customVideoSource?.addCaptureVideoSink(customVideoSink)
            mActivity.customVideoSource?.setEncodeErrorInterface {
                mActivity.runOnUiThread {
                    openDialog("错误", "当前设备硬件编码器初始化错误")
                }
            };
            fl_local_container.addView(localView)
//            mActivity.customVideoSource?.start()
            tv_local_id.text = "${dataBean.uid}(我)"
            return
        }
        fl_remote_id.visibility = View.VISIBLE
        fl_remote_container.removeAllViews()
        val remoteView = PaaSInstance.createRenderView(this.requireContext())
        val remoteCustomVideoSink = CustomVideoSink(mActivity.shouldVideoCodec)
        remoteCustomVideoSink?.setSurfaceView(remoteView as GLFrameSurfaceView)
        mActivity.createChannel?.setRemoteVideoSink(
            dataBean.uid, dataBean.streamName,
            remoteCustomVideoSink
        )
        dataBean.videoSink = remoteCustomVideoSink
        fl_remote_container.addView(remoteView)
        tv_remote_id.text = "${dataBean.uid}"

    }

    fun leave(view: View) {
        val result = mActivity.leaveChannel()
        if (kick) {
            mActivity.createChannel?.release()
            if (isAdded) {
                Navigation.findNavController(requireView()).navigateUp()
            }
        }else{
            if (result != 0 || result == null) {
                mActivity.createChannel?.release()
                Navigation.findNavController(view).navigateUp()
            }
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

    val channelEventHandler = object : MiddleRtcChannelEventHandler() {


        override fun onUserOffline(channel: IRtcChannel, userID: String, reason: Int) {

            mActivity.runOnUiThread {
                for (dataBean in mActivity.modifyList) {
                    if (dataBean.uid == userID) {
                        fl_remote_container.removeAllViews()
                        mActivity.modifyList.remove(dataBean)
                        tv_remote_id.text = ""
                        fl_remote_id.visibility = View.GONE

                    }
                }
            }
        }

        override fun onVideoSubscribeStateChanged(
            userID: String,
            streamName: String,
            state: Int,
            elapsed: Int
        ) {
            if (state == SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_ONLINE) {
                if (mActivity.modifyList.size > 2) {
                    return
                }
                for (dataBean in mActivity.modifyList) {
                    if (dataBean.uid == userID) {
                        mActivity.runOnUiThread {
                            addStream(
                                DataBean(
                                    uid = userID,
                                    streamName = streamName,
                                    isSelf = false
                                )
                            )
                        }
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

        override fun onConnectionLost(channel: IRtcChannel?) {
            mActivity.runOnUiThread {
                openDialog("已断开连接", "网络链接丢失")
            }
        }

        override fun onRemoteVideoStats(channel: IRtcChannel, stats: RemoteVideoStats) {
            mActivity.runOnUiThread {
                tv_remote_bitrate.text = "接受码流： ${stats.receivedBitrate} Kbps"
            }
        }
        override fun onError(channel: IRtcChannel?, err: Int, msg: String?) {
            when (err) {
//                ERROR_TYPE.ERR_JOIN_CHANNEL_REJECTED,
//                ERROR_TYPE.ERR_LEAVE_CHANNEL_REJECTED -> {
//
//                    mActivity.runOnUiThread {
//                        if (isAdded) {
//                            Navigation.findNavController(requireView()).navigateUp()
//                        }
//                        mActivity.createChannel?.release()
//                    }
//                }
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
                    kick = true

                    openDialog("已断开连接", "检测到你在其他设备登录\n" +
                                "请返回登录后重试。")
                }
            }

        }

        override fun onPredictedBitrateChanged(
            uid: String?,
            streamName: String?,
            newBitrate: Int,
            isLowVideo: Boolean
        ) {
            if(newBitrate == 0)return
            mActivity.runOnUiThread{
                for (dataItem in mActivity.modifyList) {
                    if (uid == dataItem.uid) {
                        if(dataItem.isSelf){
                            tv_local_prebitrate.text =
                                "预测带宽： ${NetworkUtils.convertNetSpeed(newBitrate)}"
                        }else{
                            tv_remote_prebitrate.text =
                                "预测带宽： ${NetworkUtils.convertNetSpeed(newBitrate)}"
                        }

                    }

                }
            }

        }

        override fun onRtcStats(channel: IRtcChannel, stats: RtcStats) {
            activity?.runOnUiThread{
                tv_local_up_link.text = "${stats.txKBitRate / 8}KB/S"
            }
        }

    }

    var informationInterface = object : InformationInterface() {

        override fun onLocalVideoStateChanged(state: Int, error: Int) {

            mActivity.runOnUiThread {
                if (error == LOCAL_VIDEO_STREAM_ERROR.LOCAL_VIDEO_STREAM_ERROR_DEVICE_NO_PERMISSION) {
                    //没有权限
                    openDialog("提示", "需要摄像头麦克风权限")

                }
            }
        }

        override fun onLocalAudioStateChanged(state: Int, error: Int) {
            mActivity.runOnUiThread {
                if (error == LOCAL_AUDIO_STREAM_ERROR.LOCAL_AUDIO_STREAM_ERROR_DEVICE_NO_PERMISSION) {
                    //没有权限
                    openDialog("提示", "需要摄像头麦克风权限")
                }
            }
        }

        override fun onPredictedBitrateChanged(newBitrate: Int, isLowVideo: Boolean) {

        }

    }

    val rtcStreamCallback = object : MiddleStreamCallback(){
        override fun onPredictedBitrateChanged(
            rtcStream: IRtcStream,
            newBitrate: Int,
            isLowVideo: Boolean
        ) {
            mActivity.runOnUiThread {
                tv_local_prebitrate.text =
                    "预测带宽： ${NetworkUtils.convertNetSpeed(newBitrate)}"
            }
            if (isLowVideo) return
            val currentRealBitrate = BitrateUtil.getRealBitrate(
                1280,
                720,
                15
            )
            val newbitrate = if (currentRealBitrate > newBitrate) {
                if(newBitrate < currentRealBitrate.shr(2)){
                    currentRealBitrate.shr(2)
                }else{
                    newBitrate
                }
            } else {
                currentRealBitrate
            }
            mActivity.customVideoSource?.resetBitrate(newbitrate)

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        mActivity.modifyList.clear()
        PaaSInstance.removeChannelEventHandler(channelEventHandler)
        PaaSInstanceHelper.removeListener(informationInterface)
    }
}