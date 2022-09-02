package io.kinescope.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.kinescope.sdk.network.NetworkModule
import io.kinescope.sdk.network.Repository
import io.kinescope.sdk.player.KinescopePlayer
import io.kinescope.sdk.view.KinescopePlayerView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        val playerView = findViewById<KinescopePlayerView>(R.id.kinescope_player)
        val player = KinescopePlayer(this)
        playerView.setPlayer(player)
        playerView?.setMediaUrl("https://msk-2-storage.kinescope.io/mp4/ff7c8e12-c59f-4ea0-8f0d-8a04959dfcc3/58bf17d8-c03a-4097-a450-ecde5f8c377a")
        player.play()

        //Repository.getVideo("41e3f776-90c1-4ea8-8bf7-e2fe774eaa69")
        Repository.getAll()
    }


}