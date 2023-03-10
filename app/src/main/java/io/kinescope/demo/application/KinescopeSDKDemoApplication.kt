package io.kinescope.demo.application

import android.app.Application
import io.kinescope.sdk.api.KinescopeApiHelperImpl
import io.kinescope.sdk.network.RetrofitBuilder

class KinescopeSDKDemoApplication : Application() {
    lateinit var apiHelper : KinescopeApiHelperImpl

    override fun onCreate() {
        super.onCreate()
        apiHelper = KinescopeApiHelperImpl(RetrofitBuilder.getKinescopeApi())

        //Register Kinescope SDK API key
    }
}