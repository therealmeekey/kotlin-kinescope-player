package io.kinescope.demo.live

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.kinescope.demo.R
import io.kinescope.sdk.player.KinescopeVideoPlayer
import io.kinescope.sdk.view.KinescopePlayerView

class LiveActivity : AppCompatActivity() {

    private lateinit var playerView: KinescopePlayerView
    private lateinit var player : KinescopeVideoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        player = KinescopeVideoPlayer(this)

        playerView = findViewById(R.id.player)
        playerView.setPlayer(player)

        player.loadVideo("<ID>", {
            player.play()
        }) {
            it.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        player.stop()
    }
}