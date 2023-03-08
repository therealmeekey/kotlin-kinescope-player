package io.kinescope.sdk.logger

import android.util.Log

enum class KinescopeLoggerLevel(val value:String) {
    NETWORK("KinescopeSDKNetwork"),
    PLAYER("KinescopeSDKPlayer"),
    PLAYER_VIEW("KinescopeSDKPlayerView"),
}

object KinescopeLogger {

    fun log(level:KinescopeLoggerLevel, message: String) {
        Log.d(level.value, message)
    }
}