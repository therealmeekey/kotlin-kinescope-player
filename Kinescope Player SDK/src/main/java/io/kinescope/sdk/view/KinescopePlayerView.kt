package io.kinescope.sdk.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import io.kinescope.sdk.R

class KinescopePlayerView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs), KinescopePlayerViewInterface {

    var player: ExoPlayer? = null
    var playerView: StyledPlayerView? = null


    init {
        inflate(context, R.layout.view_kinesope_player, this)
        player = ExoPlayer.Builder(context).build()
        playerView = findViewById(R.id.v_exoplayer)
        playerView?.player = player
    }

    fun intExoPlayer() {

    }

    fun setMediaUrl(url:String) {
        player?.setMediaItem(MediaItem.fromUri(url))
        player?.playWhenReady = false
        player?.prepare()
    }


    override fun play() {
        player?.play()
    }

    override fun pause() {
        player?.pause()
    }

    override fun stop() {
        player?.stop()
    }

}