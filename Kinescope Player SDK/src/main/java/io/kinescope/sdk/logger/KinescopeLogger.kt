package io.kinescope.sdk.logger

import android.util.Log

object KinescopeLogger {
    private val tag:String = "KinescopeSDK"

    fun log(message: String) {
        Log.d(tag, message)
    }
}