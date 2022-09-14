package io.kinescope.sdk.view

import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util
import io.kinescope.sdk.R
import io.kinescope.sdk.player.KinescopePlayer

class KinescopePlayerView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    companion object {
        fun switchTargetView(oldPlayerView:KinescopePlayerView, newPlayerView:KinescopePlayerView, player:KinescopePlayer) {
            //StyledPlayerView.switchTargetView()
        }

        private const val DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS = 200
        private const val MAX_UPDATE_INTERVAL_MS = 1000
    }

    private val formatBuilder: StringBuilder = StringBuilder()
    private val formatter = java.util.Formatter(formatBuilder, java.util.Locale.getDefault())

    private var kinescopePlayer: KinescopePlayer? = null
    private var exoPlayerView: StyledPlayerView? = null
    private var controlView:FrameLayout? = null

    private var positionView:TextView? = null
    private var durationView:TextView? = null
    private var timeBar:TimeBar? = null
    private var playPauseButton: View? = null

    private var scrubbing = false
    private var window = Timeline.Window()

    private var currentWindowOffset: Long = 0
    private val timeBarMinUpdateIntervalMs = DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS

    private val updateProgressRunnable = Runnable { updateProgress() }

    private val progressUpdateListener =
        StyledPlayerControlView.ProgressUpdateListener {
                position, bufferedPosition ->
        }

    private var componentListener = object: Player.Listener, OnClickListener, TimeBar.OnScrubListener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED
                )
            ) {
                updatePlayPauseButton()
            }
        }

        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            scrubbing = true
            positionView?.text = Util.getStringForTime(formatBuilder, formatter, position)
        }

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            positionView?.text = Util.getStringForTime(formatBuilder, formatter, position)
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            scrubbing = false
            if (!canceled && kinescopePlayer != null) {
                seekToTimeBarPosition(kinescopePlayer!!.exoPlayer!!, position)
            }
        }

        override fun onClick(view: View?) {
            val player: Player = kinescopePlayer?.exoPlayer ?: return

            if (playPauseButton === view) {
                dispatchPlayPause(player)
            }
        }
    }


    init {
        inflate(context, R.layout.view_kinesope_player, this)
        exoPlayerView = findViewById(R.id.view_exoplayer)
        controlView = findViewById(R.id.view_control)
        timeBar = controlView?.findViewById<KinescopeTimeBar>(R.id.kinescope_progress)
        positionView = controlView?.findViewById(R.id.kinescope_position)
        durationView = controlView?.findViewById(R.id.kinescope_duration)
        playPauseButton = controlView?.findViewById(R.id.kinescope_play_pause)
        setUIlisteners()
    }

    fun setPlayer(kinescopePlayer: KinescopePlayer) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper())
        if (this.kinescopePlayer === kinescopePlayer) return
        this.kinescopePlayer?.exoPlayer?.removeListener(componentListener)
        this.kinescopePlayer = kinescopePlayer
        kinescopePlayer.exoPlayer?.addListener(componentListener)
        exoPlayerView?.player = kinescopePlayer.exoPlayer
        updateAll()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        /*isAttachedToWindow = true
        if (isFullyVisible()) {
            controlViewLayoutManager.resetHideCallbacks()
        }*/
        updateAll()
    }


    fun setMediaUrl(url:String) {
        kinescopePlayer?.exoPlayer?.setMediaItem(MediaItem.fromUri(url))
        kinescopePlayer?.exoPlayer?.playWhenReady = false
        kinescopePlayer?.exoPlayer?.prepare()

    }

    private fun updateAll() {
        updatePlayPauseButton()
        /*
        updateNavigation()
        updateRepeatModeButton()
        updatePlaybackSpeedList()*/
        updateTimeline()
    }

    private fun updatePlayPauseButton() {
        if (!controlView!!.isVisible || !isAttachedToWindow) {
            return
        }
        if (playPauseButton != null) {
            if (shouldShowPauseButton()) {
                (playPauseButton as ImageView)
                    /*.setImageDrawable(resources.getDrawable(com.google.android.exoplayer2.ui.R.drawable.exo_styled_controls_pause))*/
                    .setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.kinescope_controls_pause ))
                //playPauseButton?.contentDescription = resources.getString(com.google.android.exoplayer2.ui.R.string.exo_controls_pause_description)
            } else {
                (playPauseButton as ImageView)
                    /*.setImageDrawable(resources.getDrawable(com.google.android.exoplayer2.ui.R.drawable.exo_styled_controls_play))*/
                    .setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.kinescope_controls_play))
                //playPauseButton?.contentDescription = resources.getString(com.google.android.exoplayer2.ui.R.string.exo_controls_play_description)
            }
        }
    }

    private fun shouldShowPauseButton(): Boolean {
        return kinescopePlayer?.exoPlayer != null && kinescopePlayer!!.exoPlayer!!.playbackState != Player.STATE_ENDED && kinescopePlayer!!.exoPlayer!!.playbackState != Player.STATE_IDLE && kinescopePlayer!!.exoPlayer!!.getPlayWhenReady()
    }

    private fun setUIlisteners() {
        exoPlayerView?.setOnClickListener {
            controlView?.isVisible = true
            updateAll()
        }
        controlView?.setOnClickListener { controlView?.isVisible = false }

        timeBar?.addListener(componentListener)
        playPauseButton?.setOnClickListener(componentListener)
    }

    private fun updateTimeline() {
        val player: Player = kinescopePlayer?.exoPlayer ?: return

        currentWindowOffset = 0
        var durationUs: Long = 0
        val timeline = player.currentTimeline
        if (!timeline.isEmpty) {
            val currentWindowIndex = player.currentMediaItemIndex
            val firstWindowIndex = currentWindowIndex
            val lastWindowIndex = currentWindowIndex
            for (i in firstWindowIndex..lastWindowIndex) {
                if (i == currentWindowIndex) {
                    currentWindowOffset = Util.usToMs(durationUs)
                }
                timeline.getWindow(i, window)
                if (window.durationUs == C.TIME_UNSET) {
                    //Assertions.checkState(!multiWindowTimeBar)
                    break
                }
                durationUs += window.durationUs
            }
        }
        val durationMs = Util.usToMs(durationUs)
        durationView?.text = Util.getStringForTime(formatBuilder, formatter, durationMs)
        timeBar?.setDuration(durationMs)
        updateProgress()
    }

    private fun updateProgress() {
        if (!controlView!!.isVisible || !isAttachedToWindow) {
            return
        }
        val player: Player? = kinescopePlayer?.exoPlayer
        var position: Long = 0
        var bufferedPosition: Long = 0
        if (player != null) {
            position = currentWindowOffset + player.contentPosition
            bufferedPosition = currentWindowOffset + player.contentBufferedPosition
        }
        if (positionView != null && !scrubbing) {
            positionView!!.text = Util.getStringForTime(formatBuilder, formatter, position)
        }
        timeBar?.setPosition(position)
        timeBar?.setBufferedPosition(bufferedPosition)
        if (progressUpdateListener != null) {
            progressUpdateListener.onProgressUpdate(position, bufferedPosition)
        }

        // Cancel any pending updates and schedule a new one if necessary.
        removeCallbacks(updateProgressRunnable)
        val playbackState = player?.playbackState ?: Player.STATE_IDLE
        if (player != null && player.isPlaying) {
            var mediaTimeDelayMs =
                if (timeBar != null) timeBar!!.preferredUpdateDelay else MAX_UPDATE_INTERVAL_MS.toLong()

            // Limit delay to the start of the next full second to ensure position display is smooth.
            val mediaTimeUntilNextFullSecondMs = 1000 - position % 1000
            mediaTimeDelayMs = Math.min(mediaTimeDelayMs, mediaTimeUntilNextFullSecondMs)

            // Calculate the delay until the next update in real time, taking playback speed into account.
            val playbackSpeed = player.playbackParameters.speed
            var delayMs =
                if (playbackSpeed > 0) (mediaTimeDelayMs / playbackSpeed).toLong() else MAX_UPDATE_INTERVAL_MS.toLong()

            // Constrain the delay to avoid too frequent / infrequent updates.
            delayMs = Util.constrainValue(
                delayMs,
                timeBarMinUpdateIntervalMs.toLong(),
                MAX_UPDATE_INTERVAL_MS.toLong()
            )
            postDelayed(updateProgressRunnable, delayMs)
        } else if (playbackState != Player.STATE_ENDED && playbackState != Player.STATE_IDLE) {
            postDelayed(
                updateProgressRunnable,
                MAX_UPDATE_INTERVAL_MS.toLong()
            )
        }
    }

    private fun seekToTimeBarPosition(player: Player, positionMs: Long) {
        var positionMs = positionMs
        var windowIndex: Int
        val timeline = player.currentTimeline
        if (!timeline.isEmpty) {
            val windowCount = timeline.windowCount
            windowIndex = 0
            while (true) {
                val windowDurationMs = timeline.getWindow(windowIndex, window).durationMs
                if (positionMs < windowDurationMs) {
                    break
                } else if (windowIndex == windowCount - 1) {
                    // Seeking past the end of the last window should seek to the end of the timeline.
                    positionMs = windowDurationMs
                    break
                }
                positionMs -= windowDurationMs
                windowIndex++
            }
        } else {
            windowIndex = player.currentMediaItemIndex
        }
        seekTo(player, windowIndex, positionMs)
        updateProgress()
    }

    private fun seekTo(player: Player, windowIndex: Int, positionMs: Long) {
        player.seekTo(windowIndex, positionMs)
    }

    private fun dispatchPlayPause(player: Player) {
        val state = player.playbackState
        if (state == Player.STATE_IDLE || state == Player.STATE_ENDED || !player.playWhenReady) {
            dispatchPlay(player)
        } else {
            dispatchPause(player)
        }
    }

    private fun dispatchPlay(player: Player) {
        val state = player.playbackState
        if (state == Player.STATE_IDLE) {
            player.prepare()
        } else if (state == Player.STATE_ENDED) {
            seekTo(player, player.currentMediaItemIndex, C.TIME_UNSET)
        }
        player.play()
    }

    private fun dispatchPause(player: Player) {
        player.pause()
    }
}