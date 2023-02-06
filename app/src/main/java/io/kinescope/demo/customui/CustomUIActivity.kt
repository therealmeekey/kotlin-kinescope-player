package io.kinescope.demo.customui

import io.kinescope.demo.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import io.kinescope.sdk.models.videos.KinescopeVideo
import io.kinescope.sdk.network.Repository
import io.kinescope.sdk.player.KinescopePlayer
import io.kinescope.sdk.view.KinescopePlayerView

class CustomUIActivity : AppCompatActivity() {
    private var isVideoFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)
        kinescopePlayer = KinescopePlayer(this.applicationContext)
    }

    lateinit var playerView:KinescopePlayerView
    lateinit var kinescopePlayer:KinescopePlayer


    override fun onStart() {
        super.onStart()
        playerView = findViewById(R.id.kinescope_player)
        kinescopePlayer.setShowFullscreen(false)
        kinescopePlayer.setShowOptions(false)
        playerView.setPlayer(kinescopePlayer)
        //playerView.setCustomControllerLayoutID(R.layout.view_custom_ui)
        Repository.getVideo("b138bf19-72fc-474b-901b-00f323899598", object : Repository.GetVideoCallback {
            override fun onResponse(value: KinescopeVideo) {
                kinescopePlayer.setVideo(value)
                kinescopePlayer.play()
            }

            override fun onFailure() {

            }
        })

        setListeners()
    }

    private fun setListeners() {
        findViewById<View>(R.id.btn_pause)?.setOnClickListener { kinescopePlayer.pause() }
        findViewById<View>(R.id.btn_play)?.setOnClickListener { kinescopePlayer.play() }
        findViewById<View>(R.id.btn_stop)?.setOnClickListener { kinescopePlayer.stop() }
        findViewById<View>(R.id.btn_seek_forward)?.setOnClickListener { kinescopePlayer.seekTo(10000)}
        findViewById<View>(R.id.btn_seek_back)?.setOnClickListener { kinescopePlayer.seekTo(-10000)}
        findViewById<View>(R.id.btn_speed_05)?.setOnClickListener { kinescopePlayer.setPlaybackSpeed(0.5f)}
        findViewById<View>(R.id.btn_speed_1)?.setOnClickListener { kinescopePlayer.setPlaybackSpeed(1f)}
        findViewById<View>(R.id.btn_speed_2)?.setOnClickListener { kinescopePlayer.setPlaybackSpeed(2f)}
    }



}