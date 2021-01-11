package com.rzrtc.paasdemo

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.rz.paas.test.inChannel.adapter.VideoData

/**
 * @author : create by yanggaosheng$
 * @time  : 2020/11/24$
 * @des  :  主要设置界面item相关提示$
 **/
fun getCameraIcon(iv: TextView, videoData: VideoData): View {
    iv.alpha = 1f
    var imageRes =
            if (videoData.closeCamera && videoData.muteCamera) {
                iv.alpha = 0.5f
                R.mipmap.icon_close_camera
            } else if (videoData.closeCamera) {
                R.mipmap.icon_close_camera
            } else if (videoData.muteCamera) {
                R.mipmap.icon_close_camera
            } else {
                R.mipmap.camera_close_icon
            }
    val drawable = ResourcesCompat.getDrawable(iv.context.resources, imageRes, iv.context.theme)!!
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    val hintText =
        if (videoData.muteCamera) {
            "关闭"
        } else {
            "开启"
        }
    iv.textSize = 10f;//dp2px(iv.context, 4).toFloat()
    iv.setText(hintText)
    iv.setCompoundDrawables(drawable, null, null, null)
    iv.setCompoundDrawablePadding(10)
    iv.setBackgroundResource(R.drawable.video_view_icon_bg)
    return iv
}

fun getCameraSwitchText(iv: TextView, videoData: VideoData): View {
    iv.alpha = 1f
    var imageRes = R.mipmap.switch_camera_icon

    val drawable = ResourcesCompat.getDrawable(iv.context.resources, imageRes, iv.context.theme)!!
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    val hintText =
        if (videoData.isFontCamera) {
            "前置"
        } else {
            "后置"
        }
    iv.text = hintText
    iv.textSize = 8f

    iv.setCompoundDrawables(drawable, null, null, null)
    iv.setCompoundDrawablePadding(10)
    iv.setBackgroundResource(R.drawable.video_view_icon_bg)
    return iv
}

fun getMicOperatorIcon(iv: TextView, videoData: VideoData):View {

    iv.alpha = 1f
    var imageRes =
        if (videoData.muteMicrophone) {
            iv.alpha = 0.9f
            R.mipmap.icon_no_remote_volume
        } else {
            R.mipmap.icon_colume_img
        }
    val drawable = ResourcesCompat.getDrawable(iv.context.resources, imageRes, iv.context.theme)!!
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    val hintText =
        if (videoData.muteMicrophone) {
            "关闭"
        } else {
            "开启"
        }
    iv.text = hintText
    iv.textSize = 10f;//dp2px(iv.context, 4).toFloat()

    iv.setCompoundDrawables(drawable, null, null, null)
    iv.setCompoundDrawablePadding(10)
    iv.setBackgroundResource(R.drawable.video_view_icon_bg)
    return iv
}

fun getDuaStreamSWitchIcon(iv: TextView, videoData: VideoData):View {

    iv.alpha = 1f
    var imageRes = R.mipmap.hd_open_icon
    val drawable = ResourcesCompat.getDrawable(iv.context.resources, imageRes, iv.context.theme)!!
    drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
    val hintText ="切换"
    iv.text = hintText
    iv.textSize = 8f

    iv.setCompoundDrawables(drawable, null, null, null)
    iv.setCompoundDrawablePadding(10)
    iv.setBackgroundResource(R.drawable.video_view_icon_bg)
    return iv
}

 fun coverChange(viewGroup: ViewGroup, imageView: ImageView, textView: TextView, data: VideoData) {
    if (data.isSelf) {
        if (data.closeCamera) {
            viewGroup.visibility = View.VISIBLE
            imageView.setImageResource(R.mipmap.close_camer_tip_img)
            textView.text = "远端视频流停止发送"
        }else{
            viewGroup.visibility = View.GONE
        }
        return
    }
    viewGroup.visibility = View.VISIBLE
    if (data.isInit) {
        imageView.setImageResource(R.mipmap.close_camer_tip_img)
        textView.text = "远端视频流停止发送"
    }else if (data.isLoading) {
        imageView.setImageResource(R.mipmap.icon_loading)
        textView.text = "加载中..."
    } else if (data.muteCamera && data.closeCamera) {
        imageView.setImageResource(R.mipmap.close_camer_tip_img)
        textView.text = "已停止拉取远端视频流\n远端视频流停止发送"
    } else if (data.closeCamera) {
        imageView.setImageResource(R.mipmap.close_camer_tip_img)
        textView.text = "远端视频流停止发送"
    } else if (data.muteCamera) {
        imageView.setImageResource(R.mipmap.close_camer_tip_img)
        textView.text = "已停止拉取远端视频流"
    } else {
        viewGroup.visibility = View.GONE

    }

}


private fun dp2px(context: Context, dp: Int): Int {

    val scale = context.resources.displayMetrics.density;
    return (dp * scale + 0.5f).toInt()

}

fun Fragment.sp2px(sp:Int):Float{
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,sp*1.0f,requireContext().resources.displayMetrics)
}