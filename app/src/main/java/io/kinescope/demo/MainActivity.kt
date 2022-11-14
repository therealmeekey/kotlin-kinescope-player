package io.kinescope.demo

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import io.kinescope.sdk.network.NetworkModule
import io.kinescope.sdk.network.Repository
import io.kinescope.sdk.player.KinescopePlayer
import io.kinescope.sdk.view.KinescopePlayerView

class MainActivity : AppCompatActivity() {
    private var isVideoFullscreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onStart() {
        super.onStart()
    }
}