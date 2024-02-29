package io.kinescope.demo.live

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.media3.common.util.UnstableApi
import io.kinescope.demo.R
import io.kinescope.sdk.player.KinescopeVideoPlayer
import io.kinescope.sdk.view.KinescopePlayerView

@UnstableApi
class LiveActivity : AppCompatActivity() {

    private lateinit var kinescopeplayer: KinescopeVideoPlayer
    private lateinit var watchLiveContainerView: LinearLayout
    private lateinit var watchLiveIdInputView: EditText
    private lateinit var watchLiveBtnView: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live)

        val playerView = findViewById<KinescopePlayerView>(R.id.player)
        watchLiveContainerView = findViewById(R.id.watch_live_ll)
        watchLiveIdInputView = findViewById(R.id.id_et)
        watchLiveBtnView = findViewById(R.id.watch_live_btn)

        kinescopeplayer = KinescopeVideoPlayer(this)
        playerView.setPlayer(kinescopeplayer)

        watchLiveIdInputView.addTextChangedListener {
            watchLiveBtnView.isEnabled = it.isNullOrEmpty().not()
        }

        watchLiveBtnView.setOnClickListener {
            tryLoadVideo(watchLiveIdInputView.text.toString())
        }
    }

    override fun onStop() {
        super.onStop()
        kinescopeplayer.stop()
    }

    private fun tryLoadVideo(id: String) {
        watchLiveContainerView.isVisible = false
        kinescopeplayer.loadVideo(id, {
            kinescopeplayer.play()
        }) {
            it.printStackTrace()
        }
    }
}