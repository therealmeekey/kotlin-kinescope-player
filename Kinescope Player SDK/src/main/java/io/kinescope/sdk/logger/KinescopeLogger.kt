package io.kinescope.sdk.logger

import android.util.Log

class KinescopeLogger {
    companion object {
        private val tag:String = "KinescopeSDK"

        fun log(message: String) {
            Log.d(tag, message)
        }
    }
}