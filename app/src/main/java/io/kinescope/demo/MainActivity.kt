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
        playerView.bindPlayer(player)
        playerView?.setMediaUrl("https://msk-1-storage.kinescope.io/mp4/65659012-b489-4505-9a74-f3d12e0ed656/08f8b309-97b0-4550-aed6-ccd751be5f79")
        player.play()

        //Repository.getVideo("41e3f776-90c1-4ea8-8bf7-e2fe774eaa69")
        Repository.getAll()
    }


}