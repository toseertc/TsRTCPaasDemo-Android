package cn.tosee.rtch264demo.utils

/**
 * @author: geh
 * @date: 2021/9/16
 */
object BitrateUtil {
    /**
     * 获取实际码率
     * @param width 分辨率宽
     * @param height 分辨率高
     * @param frameRate 帧率
     *
     * @return 实际码率
     */
    fun getRealBitrate(width: Int, height: Int, frameRate: Int): Int {
        val leng = if (width < height) width else height
        val resolutionModulus: Float = when (leng) {
            in 0..60 -> 20.0f
            in 61..80 -> 13.0f
            in 81..100 -> 8.0f
            in 101..120 -> 3.0f
            in 121..240 -> 2.4f
            in 241..360 -> 1.6f
            in 361..480 -> 1.4f
            in 481..720 -> 1.2f
            in 721..1080 -> 1f
            else -> 0.8f
        }
        val fpsModulus: Float = when (frameRate) {
            0 -> 0.2f
            in 1..9 -> 0.67f
            in 10..14 -> 0.8f
            in 15..23 -> 1f
            in 24..29 -> 1.3f
            in 30..59 -> 1.5f
            else -> 2.25f
        }

        return (width * height * resolutionModulus * fpsModulus).toInt()
    }
}