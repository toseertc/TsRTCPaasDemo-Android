package cn.tosee.rtch264demo

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import cn.tosee.rtc.IRtcChannel
import cn.tosee.rtc.capture.CameraVideoCapturer
import cn.tosee.rtc.stream.IRtcStream
import cn.tosee.rtc.stream.IRtcStreamEventHandler
import cn.tosee.rtc.video.VideoEncoderConfiguration
import cn.tosee.rtch264demo.rtc.listener.MiddleRtcChannel
import cn.tosee.rtch264demo.rtc.listener.MiddleRtcChannelEventHandler
import cn.tosee.rtch264demo.rtc.PaaSInstance
import cn.tosee.rtch264demo.bean.DataBean
import cn.tosee.rtch264demo.constant.PUBLISH_MEDIA_TYPE
import cn.tosee.rtch264demo.constant.SUBSCRIBE_STREAM_STATE
import cn.tosee.rtch264demo.constant.VIDEO_MIRROR_MODE_TYPE
import cn.tosee.rtch264demo.constant.VIDEO_STREAM_TYPE
import cn.tosee.rtch264demo.customvideo.CustomVideoSink
import cn.tosee.rtch264demo.customvideo.CustomVideoSource
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.CopyOnWriteArrayList

class MainActivity : AppCompatActivity() {
    private val PERMISSION_CODE = 1000
    var createChannel: MiddleRtcChannel? = null
    val modifyList = CopyOnWriteArrayList<DataBean>()

    var isJoinChanneled=false;
    var customVideoSource: CustomVideoSource? = null
    var createVideoStream: IRtcStream? = null
    val shouldVideoCodec = true //如果不需要使用外部编解码，则修改为false
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
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }


    fun createChannel(channelId: String) {
        if (isJoinChanneled){
            if (createVideoStream!=null) {
                createVideoStream?.unPublish()
                createVideoStream?.release()
            }
        }


        createChannel?.leaveChannel()
        createChannel?.release()
        PaaSInstance.enableLocalAudio(true)

        createChannel = PaaSInstance.createChannel(0, channelId)
        createChannel?.publish(PUBLISH_MEDIA_TYPE.PUBLISH_AUDIO)

        customVideoSource =
            CustomVideoSource(applicationContext, shouldVideoCodec, true, customEventhandler)
        createVideoStream = createChannel?.createVideoStream(
            "first",
            customVideoSource,
            if (shouldVideoCodec) VIDEO_STREAM_TYPE.VIDEO_STREAM_H264 else VIDEO_STREAM_TYPE.VIDEO_STREAM_FRAME,
            rtcStreamHandler,
            VideoEncoderConfiguration(1280, 720)
        )


    }

    fun joinChannel(userId: String): Int {
        val result = createChannel!!.joinChannel(userId)

        return result
    }

    fun leaveChannel(): Int? {

        createVideoStream?.unPublish()
        createVideoStream?.release()
        val result = createChannel?.leaveChannel()
        if (result != 0) {
            createChannel?.release()
        }
        PaaSInstance.enableLocalVideo(false)
        return result
    }

    //主要将用户信息存下来
    fun onUserJoined(userId: String, isSelf: Boolean, deviceName: String) {
        if (modifyList.size > 2) {
            return
        }
        val dataBean = DataBean(userId, isSelf = isSelf)
        if (dataBean.isSelf) {
            modifyList.add(0, dataBean)
        } else {
            modifyList.add(dataBean)
        }
    }

    //自定义流相关事件
    object rtcStreamHandler : IRtcStreamEventHandler() {}

    val customEventhandler = object : CameraVideoCapturer.CameraEventsHandler {
        override fun onCameraError(errorDescription: String?) {
//            showTipsDialog("摄像头打开发生错误，请尝试重新开启摄像头或重新加入频道", 1)
        }

        override fun onCameraDisconnected() {

        }

        override fun onCameraFreezed(errorDescription: String?) {
        }

        override fun onCameraOpening(cameraName: String?) {
        }

        override fun onFirstFrameAvailable() {
        }

        override fun onCameraClosed() {
        }
    }

    val channelEventHandler = object : MiddleRtcChannelEventHandler() {
        override fun onJoinChannelSuccess(channel: IRtcChannel, userID: String, elapsed: Int) {
            isJoinChanneled=true;
            createVideoStream?.publish()
            onUserJoined(userID, true, "first")
        }

        override fun onUserJoined(channel: IRtcChannel, userId: String, elapsed: Int) {
        }

        override fun onVideoSubscribeStateChanged(
            userID: String,
            streamName: String,
            state: Int,
            elapsed: Int
        ) {
            if (state == SUBSCRIBE_STREAM_STATE.SUBSCRIBE_STREAM_STATE_ONLINE) {
                if (modifyList.size > 2) {
                    return
                }
                onUserJoined(userID, false, streamName)
            }
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
            } else if (permission == Manifest.permission.CAMERA) {

            }

        }

    }
}