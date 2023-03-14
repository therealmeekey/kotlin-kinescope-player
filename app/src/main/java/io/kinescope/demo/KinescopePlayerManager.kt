package io.kinescope.demo

import android.content.Context
import io.kinescope.sdk.player.KinescopePlayerOptions
import io.kinescope.sdk.player.KinescopeVideoPlayer

class KinescopePlayerManager {
    companion object {
        private var kinescopeVideoPlayer: KinescopeVideoPlayer? = null

        fun getInstance(context: Context, options: KinescopePlayerOptions? = null ):KinescopeVideoPlayer {
            if (kinescopeVideoPlayer == null) {
                kinescopeVideoPlayer = KinescopeVideoPlayer(context, options ?: KinescopePlayerOptions())
            }
            return kinescopeVideoPlayer!!
        }
    }
}