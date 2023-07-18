package io.kinescope.demo.application

import android.app.Application
import io.kinescope.sdk.api.KinescopeApiHelper
import io.kinescope.sdk.api.KinescopeApiHelperImpl
import io.kinescope.sdk.network.RetrofitBuilder

class KinescopeSDKDemoApplication : Application() {
    lateinit var apiHelper : KinescopeApiHelper

    override fun onCreate() {
        super.onCreate()
        //Register Kinescope SDK API key here
        apiHelper = KinescopeApiHelperImpl(RetrofitBuilder.getKinescopeApi("bc50167b-e868-47e4-a55c-07208ef15b22"))

    }
}