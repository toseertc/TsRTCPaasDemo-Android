package cn.tosee.rtcdemo.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.tosee.rtc.IRtcChannel
import cn.tosee.rtc.test.pass_sdk.middle.MiddleRtcChannelEventHandler
import cn.tosee.rtcdemo.*
import cn.tosee.rtcdemo.const.AppConstant
import cn.tosee.rtcdemo.const.ERROR_TYPE
import cn.tosee.rtcdemo.listener.InformationInterface
import kotlinx.android.synthetic.main.layout_login_fragment.*

class LoginFragment : Fragment() {
    lateinit var dialog: TipsDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_login_fragment, container, false)
    }

    lateinit var mActivity: MainActivity


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        PaaSInstanceHelper.addListener(informationInterface)
        PaaSInstance.addChannelEventHandler(channelEventHandler)
        val chanelId = SharePreUtils.getInstance(context).getValue(AppConstant.CHANNEL_ID, "")
        val userId = SharePreUtils.getInstance(context).getValue(AppConstant.USER_ID, "")
        channel_id_tv.setText(chanelId)
        user_id_tv.setText(userId)

        btn_join.setOnClickListener {
            val channelId = channel_id_tv.text.toString()
            val userId = user_id_tv.text.toString()
            if (TextUtils.isEmpty(channelId)) {
                openDialog("进入频道失败", "当前频道ID不合法")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(userId)) {
                openDialog("进入频道失败", "当前用户ID不合法")
                return@setOnClickListener
            }
            if (UiUtils.isFastDoubleClick) {
                return@setOnClickListener
            }
            SharePreUtils.getInstance(context).setValue(AppConstant.CHANNEL_ID, channelId)
            SharePreUtils.getInstance(context).setValue(AppConstant.USER_ID, userId)

            mActivity = activity as MainActivity
            mActivity.createChannel(channelId)
            val result = mActivity.joinChannel(userId)
            if (result == -2) {
                openDialog("进入频道失败", "当前用户ID不合法")
            }


        }
    }


    fun openDialog(title: String, msg: String) {
        dialog = TipsDialog(requireContext())
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setYesOnclickListener("确定") {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    var informationInterface = object : InformationInterface() {

        override fun onError(err: Int, msg: String) {
            activity?.runOnUiThread {
                if (err == ERROR_TYPE.ERR_INVALID_APP_ID) {
                    //appid不正确
                    openDialog("进入频道失败", "当前Appid不合法")
                }
                if (err == ERROR_TYPE.ERR_INVALID_CHANNEL_ID) {
                    openDialog("进入频道失败", "当前频道ID不合法")
                }
                if (err == ERROR_TYPE.ERR_VDM_CAPTURE_FAILED) {
                    openDialog("摄像头发生错误", "摄像头打开失败；请尝试重新打开摄像头或者退出频道重进")
                }
            }

        }

    }

    val channelEventHandler = object : MiddleRtcChannelEventHandler() {
        override fun onJoinChannelSuccess(channel: IRtcChannel, userID: String, elapsed: Int) {
            activity?.runOnUiThread {
                findNavController().navigate(R.id.action_loading_to_communication)
            }
        }

        override fun onError(channel: IRtcChannel?, err: Int, msg: String?) {
            when (err) {
                ERROR_TYPE.ERR_LOOKUP_SCHEDULE_SERVER_TIMEOUT,
                ERROR_TYPE.ERR_INVALID_APP_ID,
                ERROR_TYPE.ERR_LOOKUP_SCHEDULE_SERVER_FAILED,
                ERROR_TYPE.ERR_NO_SCHEDULE_SERVER_RESOURCES,
                ERROR_TYPE.ERR_LOOKUP_SERVER_TIMEOUT,
                ERROR_TYPE.ERR_NO_AVAILABLE_SERVER_RESOURCES ->
                    activity?.runOnUiThread {
                        openDialog("进入频道失败", "获取服务器资源失败 \n 错误码： $err")
                    }

            }
        }
    }


}