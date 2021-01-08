package com.rzrtc.paasdemo

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.rz.paas.IRtcChannel
import com.rz.paas.test.pass_sdk.middle.MiddleRtcChannel
import com.rz.paas.test.pass_sdk.middle.MiddleRtcChannelEventHandler
import com.rz.paas.utils.Constant
import com.rz.paas.video.VideoEncoderConfiguration
import com.rzrtc.paasdemo.bean.DataBean
import com.rzrtc.paasdemo.const.AppConstant
import com.rzrtc.paasdemo.const.VIDEO_MIRROR_MODE_TYPE
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CopyOnWriteArrayList

class MainActivity : AppCompatActivity() {
    private val PERMISSION_CODE = 1000
    var createChannel: MiddleRtcChannel? = null
    val modifyList = CopyOnWriteArrayList<DataBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        PaaSInstance.addChannelEventHandler(channelEventHandler)
        val appBarConfiguration = AppBarConfiguration.Builder().setFallbackOnNavigateUpListener {
            finish()
            true
        }.build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        supportActionBar?.hide()
        requestAllPermission()


    }

    override fun onStart() {
        super.onStart()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }

    fun getVideoEncoderConfiguration(): VideoEncoderConfiguration {
        var configuration = VideoEncoderConfiguration(640, 480)
        configuration.frameRate = 15
        configuration.mirrorMode = VIDEO_MIRROR_MODE_TYPE.VIDEO_MIRROR_MODE_AUTO//自动
        configuration.orientationMode =
            VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_LANDSCAPE.ordinal
        return configuration
    }

    fun createChannel(channelId: String) {
        if (createChannel != null) {
            createChannel?.release()
        }
        createChannel = PaaSInstance.createChannel(0, channelId)
        PaaSInstance.enableDualStreamMode(true)
        PaaSInstance.setVideoEncoderConfiguration(getVideoEncoderConfiguration())
        createChannel?.publish()
    }

    fun joinChannel(userId: String): Int {
        return createChannel!!.joinChannel(userId)
    }
    fun leaveChannel():Int? {
        val result = createChannel?.leaveChannel()
        if (result != 0) {
            createChannel?.release()
        }
        PaaSInstance.enableLocalVideo(false)
        return result
    }

    //主要将用户信息存下来
    fun onUserJoined(userId: String, channelId: String, isSelf: Boolean) {
        val dataBean = DataBean(userId, channelId = channelId, isSelf = isSelf)
        if (dataBean.isSelf) {
            modifyList.add(0, dataBean)
        } else {
            modifyList.add(dataBean)
        }
    }

    val channelEventHandler = object : MiddleRtcChannelEventHandler() {
        override fun onJoinChannelSuccess(channel: IRtcChannel, userID: String, elapsed: Int) {
            onUserJoined(userID, channel.channelId, true)
        }

        override fun onUserJoined(channel: IRtcChannel, userId: String, elapsed: Int) {
            onUserJoined(userId, channel.channelId, false)
        }
    }

    open fun requestAllPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
            ),
            PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        for (i in permissions.indices) {
            val permission = permissions[i]!!
            val result = grantResults[i]
            if (permission == Manifest.permission.RECORD_AUDIO) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    goSetting(false)
//                } else {
//                    microphonePermissionGenerated()
//                }
            } else if (permission == Manifest.permission.CAMERA) {
//                if (result != PackageManager.PERMISSION_GRANTED) {
//                    goSetting(true)
//                } else {
//                    cameraPermissionGenerated()
//
//                }
            }

        }

    }

}