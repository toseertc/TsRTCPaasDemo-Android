package cn.tosee.rtcdemo

import android.app.Application

import cn.tosee.rtcdemo.const.AppConstant


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