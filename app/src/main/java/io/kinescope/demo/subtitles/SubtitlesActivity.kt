package io.kinescope.demo.subtitles

import io.kinescope.demo.R
import io.kinescope.demo.VideosAdapter
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import io.kinescope.sdk.models.videos.KinescopeVideo
import io.kinescope.sdk.network.Repository
import io.kinescope.sdk.player.KinescopePlayer
import io.kinescope.sdk.player.KinescopePlayerOptions
import io.kinescope.sdk.view.KinescopePlayerView

class SubtitlesActivity : AppCompatActivity() {
    private var isVideoFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subtitles)
        kinescopePlayer = KinescopePlayer(this.applicationContext)
    }

    lateinit var playerView:KinescopePlayerView
    lateinit var fullscreenPlayerView:KinescopePlayerView
    lateinit var kinescopePlayer:KinescopePlayer



    override fun onStart() {
        super.onStart()
        playerView = findViewById(R.id.kinescope_player)
        kinescopePlayer.setShowSubtitles(true)
        fullscreenPlayerView = findViewById(R.id.v_kinescope_player_fullscreen)
        playerView.setIsFullscreen(false)
        fullscreenPlayerView.setIsFullscreen(true)
        playerView.setPlayer(kinescopePlayer)
        playerView.onFullscreenButtonCallback = {toggleFullscreen()}
        fullscreenPlayerView.onFullscreenButtonCallback = {toggleFullscreen()}
        Repository.getVideo("a7c69588-2473-4067-a9cc-250392f5e89e", object : Repository.GetVideoCallback {
            override fun onResponse(value: KinescopeVideo) {
                kinescopePlayer.setVideo(value)
                kinescopePlayer.play()
            }

            override fun onFailure() {

            }
        })
    }

    private fun setFullscreen(fullscreen: Boolean) {
        if (fullscreen) {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE

            KinescopePlayerView.switchTargetView(playerView, fullscreenPlayerView, kinescopePlayer)

        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        and View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            } else {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                        and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
            }

            KinescopePlayerView.switchTargetView(fullscreenPlayerView, playerView, kinescopePlayer)
        }
    }

    private fun toggleFullscreen() {
        if (isVideoFullscreen) {
            setFullscreen(false)
            if (supportActionBar != null) {
                supportActionBar?.show()
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            isVideoFullscreen = false
        } else {
            setFullscreen(true)
            if (supportActionBar != null) {
                supportActionBar?.hide()
            }
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            isVideoFullscreen = true
        }
        fullscreenPlayerView.isVisible = isVideoFullscreen
    }

    override fun onBackPressed() {
        if (isVideoFullscreen) {
            toggleFullscreen()
            return
        }
        super.onBackPressed()
    }


}