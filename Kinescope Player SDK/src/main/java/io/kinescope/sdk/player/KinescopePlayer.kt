package io.kinescope.sdk.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import io.kinescope.sdk.logger.KinescopeLogger
import io.kinescope.sdk.models.videos.KinescopeVideo

class KinescopePlayer(context:Context) {

    var exoPlayer: ExoPlayer? = null
    private var video:KinescopeVideo? = null

    init {
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    fun setVideo(video: KinescopeVideo) {
        this.video = video
        setMediaUrl(video.assets.first().url)
    }

    fun getVideo():KinescopeVideo? = video

    private fun setMediaUrl(url:String) {
        exoPlayer?.setMediaItem(MediaItem.fromUri(url))
        exoPlayer?.playWhenReady = false
        exoPlayer?.prepare()
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