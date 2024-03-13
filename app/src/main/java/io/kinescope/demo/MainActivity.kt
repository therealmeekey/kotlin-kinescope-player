package io.kinescope.demo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import io.kinescope.demo.customui.CustomUIActivity
import io.kinescope.demo.live.LiveActivity
import io.kinescope.demo.playlist.PlaylistActivity
import io.kinescope.demo.subtitles.SubtitlesActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // https://developer.android.com/reference/android/view/Display#FLAG_SECURE
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE);

        //val btnFullscreen = findViewById<AppCompatButton>(R.id.btn_fullscreen)
        val btnPlaylist = findViewById<AppCompatButton>(R.id.btn_playlist)
        val btnSubtitles = findViewById<AppCompatButton>(R.id.btn_subtitles)
        val btnCustomUI = findViewById<AppCompatButton>(R.id.btn_custom_ui)
        val btnLive = findViewById<AppCompatButton>(R.id.btn_live)

        /*btnFullscreen.setOnClickListener {
            val intent =  Intent(this, FullscreenActivity::class.java)
            startActivity(intent);
        }*/

        btnPlaylist.setOnClickListener {
            val intent =  Intent(this, PlaylistActivity::class.java)
            startActivity(intent);
        }

        btnSubtitles.setOnClickListener {
            val intent =  Intent(this, SubtitlesActivity::class.java)
            startActivity(intent);
        }

        btnCustomUI.setOnClickListener {
            val intent =  Intent(this, CustomUIActivity::class.java)
            startActivity(intent);
        }

        btnLive.setOnClickListener {
            val intent = Intent(this, LiveActivity::class.java)
            startActivity(intent)
        }
    }


    override fun onStart() {
        super.onStart()
    }
}