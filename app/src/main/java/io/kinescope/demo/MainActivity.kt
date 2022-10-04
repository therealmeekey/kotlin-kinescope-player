package io.kinescope.demo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
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
        val videosView = findViewById<RecyclerView>(R.id.rv_videos)
        val player = KinescopePlayer(this)
        playerView.setPlayer(player)

        val adapter = VideosAdapter() {
            player.setVideo(it)
            player.play()
        }

        videosView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        videosView.adapter = adapter
        Repository.getAll(object : Repository.GetAllVideosCallback {
            override fun onResponse(value: KinescopeAllVideosResponse) {
                adapter.updateData(value.data)
            }

            override fun onFailure() {

            }
        })
    }


}