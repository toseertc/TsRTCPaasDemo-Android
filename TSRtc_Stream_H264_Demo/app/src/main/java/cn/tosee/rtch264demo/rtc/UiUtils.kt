package cn.tosee.rtch264demo.rtc

/**
 * @author : create by yanggaosheng$
 * @time  : 2020/12/24$
 * @des  :  $
 **/
object UiUtils {
    private var lastClickTime: Long = 0
    val isFastDoubleClick: Boolean
        get() {
            val time = System.currentTimeMillis()
            val timeD = time - lastClickTime
            if (timeD < 1000) {
                return true
            }
            lastClickTime = time
            return false
        }
}