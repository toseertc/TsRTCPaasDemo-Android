package com.rzrtc.h264demo

import android.app.Application
import com.rzrtc.h264demo.constant.AppConstant
import com.rzrtc.h264demo.rtc.PaaSInstanceHelper



class PaaSApplication : Application() {
    companion object {
        val TAG = "PaaSApplication"
    }

    override fun onCreate() {
        super.onCreate()
        PaaSInstanceHelper.init(applicationContext,codecPriority=0,privateServiceUrl = AppConstant.devEnv,appId=AppConstant.DEFAULT_APPID, reInit = true)
    }


    override fun onTerminate() {
        super.onTerminate()
        PaaSInstanceHelper.release()

    }
}