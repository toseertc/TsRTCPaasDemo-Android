package com.rzrtc.paasdemo

import android.app.Application

import com.rzrtc.paasdemo.const.AppConstant


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