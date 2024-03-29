package io.kinescope.demo.customui

import io.kinescope.demo.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import io.kinescope.demo.KinescopeViewModel
import io.kinescope.demo.application.KinescopeSDKDemoApplication

import io.kinescope.sdk.player.KinescopeVideoPlayer
import io.kinescope.sdk.view.KinescopePlayerView

class CustomUIActivity : AppCompatActivity() {
    private var isVideoFullscreen = false

    private val viewModel: KinescopeViewModel by viewModels  {
        KinescopeViewModel.Factory((application as KinescopeSDKDemoApplication).apiHelper)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom)
        kinescopeVideoPlayer = KinescopeVideoPlayer(this.applicationContext)
        viewModel.getKinescopeVideo("b138bf19-72fc-474b-901b-00f323899598")
    }

    lateinit var playerView:KinescopePlayerView
    lateinit var kinescopeVideoPlayer:KinescopeVideoPlayer


    override fun onStart() {
        super.onStart()
        playerView = findViewById(R.id.kinescope_player)
        kinescopeVideoPlayer.setShowFullscreen(false)
        kinescopeVideoPlayer.setShowOptions(false)
        playerView.setPlayer(kinescopeVideoPlayer)
        //playerView.setCustomControllerLayoutID(R.layout.view_custom_ui)

        viewModel.video.observe(this) {
            kinescopeVideoPlayer.setVideo(it)
            kinescopeVideoPlayer.play()
        }
        setListeners()
    }

    private fun setListeners() {
        findViewById<View>(R.id.btn_pause)?.setOnClickListener { kinescopeVideoPlayer.pause() }
        findViewById<View>(R.id.btn_play)?.setOnClickListener { kinescopeVideoPlayer.play() }
        findViewById<View>(R.id.btn_stop)?.setOnClickListener { kinescopeVideoPlayer.stop() }
        findViewById<View>(R.id.btn_seek_forward)?.setOnClickListener { kinescopeVideoPlayer.seekTo(10000)}
        findViewById<View>(R.id.btn_seek_back)?.setOnClickListener { kinescopeVideoPlayer.seekTo(-10000)}
        findViewById<View>(R.id.btn_speed_05)?.setOnClickListener { kinescopeVideoPlayer.setPlaybackSpeed(0.5f)}
        findViewById<View>(R.id.btn_speed_1)?.setOnClickListener { kinescopeVideoPlayer.setPlaybackSpeed(1f)}
        findViewById<View>(R.id.btn_speed_2)?.setOnClickListener { kinescopeVideoPlayer.setPlaybackSpeed(2f)}
    }



}