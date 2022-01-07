package cn.tosee.rtcdemo.const

object ERROR_TYPE {
    /** 5: The request to join the channel is rejected.
     *
     * - This error usually occurs when the user is already in the channel, and still calls the method to join the channel.
     */
   val ERR_JOIN_CHANNEL_REJECTED = 5
    /** 6: The request to leave the channel is rejected.
    This error usually occurs:
    - When the user has left the channel and still calls leaveChannel to leave the channel.
    - When the user has not joined the channel and still calls leaveChannel to leave the channel.
     */
  val   ERR_LEAVE_CHANNEL_REJECTED = 6

    val ERR_VDM_CAPTURE_FAILED = 1504
    val ERR_LOOKUP_SCHEDULE_SERVER_TIMEOUT = 101
    val ERR_INVALID_APP_ID = 102

    val ERR_INVALID_CHANNEL_ID = 103

    val ERR_LOOKUP_SCHEDULE_SERVER_FAILED = 104

    /** 105: No available schedule server resources.
     */
    val ERR_NO_SCHEDULE_SERVER_RESOURCES = 105

    /** 106: Lookup server time out
     */
    val ERR_LOOKUP_SERVER_TIMEOUT = 106

    /** 107: No available server
     */
    val ERR_NO_AVAILABLE_SERVER_RESOURCES = 107
}

object CONNECTION_STATE_TYPE {
    val CONNECTION_STATE_DISCONNECTED = 1  //1: 网络连接断开。
    val CONNECTION_STATE_CONNECTING = 2    //2: 建立网络连接中。
    val CONNECTION_STATE_CONNECTED = 3     //3: 网络已连接。
    val CONNECTION_STATE_RECONNECTING = 4  //4: 重新建立网络连接中。
    val CONNECTION_STATE_FAILED = 5        //5: 网络连接失败。
}

object CONNECTION_CHANGED_REASON_TYPE {
    val CONNECTION_CHANGED_CONNECTING = 0                  //0: 建立网络连接中。
    val CONNECTION_CHANGED_JOIN_SUCCESS = 1                //1: 成功加入频道。
    val CONNECTION_CHANGED_INTERRUPTED = 2                 //2: 网络连接中断。
    val CONNECTION_CHANGED_BANNED_BY_SERVER = 3            //3: 网络连接被服务器禁止。
    val CONNECTION_CHANGED_JOIN_FAILED = 4                 //4: 加入频道失败。SDK 在尝试加入频道 20 分钟后还是没能加入频道，会返回该状态，并停止尝试重连。
    val CONNECTION_CHANGED_LEAVE_CHANNEL = 5               //5: 离开频道。
    val CONNECTION_CHANGED_INVALID_CHANNEL_NAME = 6        //6: 不是有效的频道名。请更换有效的频道名重新加入频道。
    val CONNECTION_CHANGED_REJECTED_BY_SERVER = 7          //7: 此用户被服务器禁止。
    val CONNECTION_CHANGED_CLIENT_IP_ADDRESS_CHANGED = 8   //8: 客户端 IP 地址变更，可能是由于网络类型，或网络运营商的 IP 或端口发生改变引起。
    val CONNECTION_CHANGED_KEEP_ALIVE_TIMEOUT = 9          //9: SDK 和服务器连接保活超时，进入自动重连状态 CONNECTION_STATE_RECONNECTING(4)。
    val CONNECTION_CHANGED_INVALID_APP_ID = 10             //10: 不是有效的 APP ID。请更换有效的 APP ID 重新加入频道。
};
object SUBSCRIBE_STREAM_STATE {
    val SUBSCRIBE_STREAM_STATE_OFFLINE = 1       //流下线
    val SUBSCRIBE_STREAM_STATE_ONLINE = 2         //流上线
    val SUBSCRIBE_STREAM_STATE_NO_SEND = 3        //流上线但没发送
    val SUBSCRIBE_STREAM_STATE_NO_RECV = 4        //流上线且发送但不接收
    val SUBSCRIBE_STREAM_STATE_NO_SEND_RECV = 5//流上线但没发送且不接收
    val SUBSCRIBE_STREAM_STATE_SUBSCRIBING = 6   //正在订阅
    val SUBSCRIBE_STREAM_STATE_SUBSCRIBED = 7    //订阅成功
    val SUBSCRIBE_STREAM_STATE_FROZEN = 8       //卡顿中
    val SUBSCRIBE_STREAM_STATE_FIRST_FRAME = 9
}

object VIDEO_MIRROR_MODE_TYPE {
    val VIDEO_MIRROR_MODE_AUTO = 0     //0：自动
    val VIDEO_MIRROR_MODE_ENABLED = 1   //1：镜像显示
    val VIDEO_MIRROR_MODE_DISABLED = 2  //2：非镜像显示
}

object PUBLISH_MEDIA_TYPE {
    val PUBLISH_INIT = 0           //不可用
    val PUBLISH_AUDIO = 1          //将默认音频发布到该频道中
    val PUBLISH_VIDEO = 2          //将默认视频发布到该频道中
    val PUBLISH_AUDIO_VIDEO = 3     //将默认音视频流都发布到该频道中
};

object LOCAL_VIDEO_STREAM_ERROR {
    val LOCAL_VIDEO_STREAM_ERROR_OK = 0                    //0: 本地视频状态正常。
    val LOCAL_VIDEO_STREAM_ERROR_FAILURE = 1               //1: 出错原因不明确。
    val LOCAL_VIDEO_STREAM_ERROR_DEVICE_NO_PERMISSION = 2  //2: 没有权限启动本地视频采集设备。
    val LOCAL_VIDEO_STREAM_ERROR_DEVICE_BUSY = 3           //3: 本地视频采集设备正在使用中。
    val LOCAL_VIDEO_STREAM_ERROR_CAPTURE_FAILURE = 4       //4: 本地视频采集失败，建议检查采集设备是否正常工作。
    val LOCAL_VIDEO_STREAM_ERROR_ENCODE_FAILURE = 5        //5: 地视频编码失败。
}

//本地音频流错误码
object LOCAL_AUDIO_STREAM_ERROR {
    val LOCAL_AUDIO_STREAM_ERROR_OK = 0                    //0: 本地音频状态正常。
    val LOCAL_AUDIO_STREAM_ERROR_FAILURE = 1               //1: 本地音频出错原因不明确。
    val LOCAL_AUDIO_STREAM_ERROR_DEVICE_NO_PERMISSION = 2  //2: 没有权限启动本地音频录制设备。
    val LOCAL_AUDIO_STREAM_ERROR_DEVICE_BUSY = 3           //3: 本地音频录制设备已经在使用中。
    val LOCAL_AUDIO_STREAM_ERROR_RECORD_FAILURE = 4        //4: 本地音频录制失败，建议你检查录制设备是否正常工作。
    val LOCAL_AUDIO_STREAM_ERROR_ENCODE_FAILURE = 5        //5: 本地音频编码失败。
};
//设置视频显示模式。
object RENDER_MODE_TYPE {
    val RENDER_MODE_HIDDEN = 1  //1：视频尺寸等比缩放。优先保证视窗被填满。因视频尺寸与显示视窗尺寸不一致而多出的视频将被截掉。
    val RENDER_MODE_FIT = 2     //2：视频尺寸等比缩放。优先保证视频内容全部显示。因视频尺寸与显示视窗尺寸不一致造成的视窗未被填满的区域填充黑色。
    val RENDER_MODE_FILL = 3    //3：视频尺寸进行缩放和拉伸以充满显示视窗。
};

//视频大小流
object REMOTE_VIDEO_STREAM_TYPE {
    val REMOTE_VIDEO_STREAM_HIGH = 0   //0：视频大流
    val REMOTE_VIDEO_STREAM_LOW = 1    //1：视频小流
};

//频道场景
object CHANNEL_PROFILE_TYPE {
    val CHANNEL_PROFILE_COMMUNICATION = 0       //0：通信场景。该场景下，频道内所有用户都可以发布和接收音、视频流。适用于语音通话、视频群聊等应用场景。
    val CHANNEL_PROFILE_LIVE_BROADCASTING = 1   //1：直播场景。该场景有主播和观众两种用户角色，可以通过 setClientRole 设置。主播可以发布和接收音视频流，观众直接接收流。适用于语聊房、视频直播、互动大班课等应用场景。
};

//用户角色
object CLIENT_ROLE_TYPE {
    val CLIENT_ROLE_BROADCASTER = 1   //1：主播。主播可以发流也可以收流。
    val CLIENT_ROLE_AUDIENCE = 2     //2：（默认）观众。观众只能收流不能发流。
};
//本地视频状态
object LOCAL_VIDEO_STREAM_STATE {
    val LOCAL_VIDEO_STREAM_STATE_STOPPED = 0         //0: 本地视频默认初始状态。
    val LOCAL_VIDEO_STREAM_STATE_CAPTURING = 1       //1: 本地视频启动成功。
    val LOCAL_VIDEO_STREAM_STATE_FAILED = 2          //2: 本地视频启动失败。
    val LOCAL_VIDEO_STREAM_STATE_SENDING = 3         //3: 正在发送媒体数据
    val LOCAL_VIDEO_STREAM_STATE_NO_SEND = 4         //4: 不发送媒体数据
}

//本地音频流发布状态
object LOCAL_AUDIO_STREAM_STATE {
    val LOCAL_AUDIO_STREAM_STATE_STOPPED = 0         //0: 本地音频默认初始状态。
    val LOCAL_AUDIO_STREAM_STATE_RECORDING = 1       //1: 本地音频录制设备启动成功。
    val LOCAL_AUDIO_STREAM_STATE_FAILED = 2         //2: 本地音频启动失败。
    val LOCAL_AUDIO_STREAM_STATE_SENDING = 3         //3: 正在发送媒体数据
    val LOCAL_AUDIO_STREAM_STATE_NO_SEND = 4         //4: 不发送媒体数据
};