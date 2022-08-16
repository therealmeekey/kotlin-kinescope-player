package io.kinescope.sdk.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackParameters
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.PlayerView.SHOW_BUFFERING_ALWAYS
import com.google.android.exoplayer2.ui.StyledPlayerView
import io.kinescope.sdk.R
import io.kinescope.sdk.logger.KinescopeLogger
import io.kinescope.sdk.player.KinescopePlayer

class KinescopePlayerView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {

    companion object {
        fun switchTargetView(oldPlayerView:KinescopePlayerView, newPlayerView:KinescopePlayerView, player:KinescopePlayer) {
            //StyledPlayerView.switchTargetView()
        }
    }

    var kinescopePlayer: KinescopePlayer? = null
    var exoPlayerView: StyledPlayerView? = null


    init {
        inflate(context, R.layout.view_kinesope_player, this)
        exoPlayerView = findViewById(R.id.v_exoplayer)
        exoPlayerView?.setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING)
        setUIlisteners()
    }

    fun bindPlayer(kinescopePlayer: KinescopePlayer) {
        this.kinescopePlayer = kinescopePlayer
        exoPlayerView?.player = kinescopePlayer.exoPlayer
    }


    fun setMediaUrl(url:String) {
        kinescopePlayer?.exoPlayer?.setMediaItem(MediaItem.fromUri(url))
        kinescopePlayer?.exoPlayer?.playWhenReady = false
        kinescopePlayer?.exoPlayer?.prepare()
    }


    /*override fun play() {
        kineScopePlayer?.play()
    }

    override fun pause() {
        kineScopePlayer?.pause()
    }

    override fun stop() {
        kineScopePlayer?.stop()
    }

    fun seek(toMilliSeconds:Long) {
        kineScopePlayer?.seekTo(toMilliSeconds)
    }

    fun setPlaybackSpeed(speed:Float) {
        kineScopePlayer?.setPlaybackSpeed(speed)
    }*/

    fun setUIlisteners() {
        /*playerView?.findViewById<ImageButton>(R.id.kinescope_play)?.setOnClickListener {
            if (player!!.isPlaying) {
                pause()
            }
            else {
                play()
            }
        }*/

        exoPlayerView?.setFullscreenButtonClickListener {

        }

        kinescopePlayer?.exoPlayer?.addListener(object :Player.Listener {
            override fun onVolumeChanged(volume: Float) {
                super.onVolumeChanged(volume)
                KinescopeLogger.log("Volume changed to: $volume")
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                KinescopeLogger.log("Is playing: $isPlaying")
            }

            override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters) {
                super.onPlaybackParametersChanged(playbackParameters)
                KinescopeLogger.log("Playback speed changed to $playbackParameters.speed")
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                oldPosition
                newPosition
                super.onPositionDiscontinuity(oldPosition, newPosition, reason)
            }

        })
    }

}