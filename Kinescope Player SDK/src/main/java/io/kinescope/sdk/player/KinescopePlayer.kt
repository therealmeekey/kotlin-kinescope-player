package io.kinescope.sdk.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import io.kinescope.sdk.logger.KinescopeLogger

class KinescopePlayer(context:Context) {

    var exoPlayer: ExoPlayer? = null

    init {
        exoPlayer = ExoPlayer.Builder(context).build()
    }


    fun play() {
        exoPlayer?.play()
        KinescopeLogger.log("Play")
    }

    fun pause() {
        exoPlayer?.pause()
        KinescopeLogger.log("Pause")
    }

    fun stop() {
        exoPlayer?.stop()
        KinescopeLogger.log("Stop")
    }

    fun seekTo(toMilliSeconds:Long) {
        exoPlayer?.seekTo(toMilliSeconds)
        KinescopeLogger.log("seek to ${toMilliSeconds / 1000} seconds" )
    }

    fun setPlaybackSpeed(speed:Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        KinescopeLogger.log("Playback speed changed to $speed")
    }
}