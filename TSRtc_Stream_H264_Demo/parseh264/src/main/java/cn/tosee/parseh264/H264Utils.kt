package cn.tosee.parseh264

class H264Utils {

    external  fun getWidth_Height_fromPacket(packets: ByteArray,length:Int):Boolean

    external fun getWidth():Int
    external fun getHeight():Int

    companion object {
        // Used to load the 'native-lib' library on application startup.
        init {
            System.loadLibrary("native-lib")
        }
    }

}