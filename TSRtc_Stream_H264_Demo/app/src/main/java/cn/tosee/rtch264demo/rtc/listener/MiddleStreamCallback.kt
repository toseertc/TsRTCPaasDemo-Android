package cn.tosee.rtch264demo.rtc.listener

import cn.tosee.rtc.stream.IRtcStream

abstract class MiddleStreamCallback {
    open fun onPredictedBitrateChanged(rtcStream: IRtcStream,  newBitrate:Int , isLowVideo:Boolean){}
}