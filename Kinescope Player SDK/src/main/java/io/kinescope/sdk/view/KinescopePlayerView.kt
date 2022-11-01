package io.kinescope.sdk.view

import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.Timeline
import com.google.android.exoplayer2.ui.StyledPlayerControlView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.util.Assertions
import com.google.android.exoplayer2.util.Util
import io.kinescope.sdk.R
import io.kinescope.sdk.adapter.KinescopeSettingsAdapter
import io.kinescope.sdk.models.videos.KinescopeVideo
import io.kinescope.sdk.player.KinescopePlayer
import io.kinescope.sdk.utils.animateRotation

class KinescopePlayerView(context: Context, attrs: AttributeSet?) : ConstraintLayout(context, attrs) {
    companion object {
        fun switchTargetView(oldPlayerView:KinescopePlayerView, newPlayerView:KinescopePlayerView, player:KinescopePlayer) {
            if (oldPlayerView === newPlayerView) {
                return
            }
            // We attach the new view before detaching the old one because this ordering allows the player
            // to swap directly from one surface to another, without transitioning through a state where no
            // surface is attached. This is significantly more efficient and achieves a more seamless
            // transition when using platform provided video decoders.
            // We attach the new view before detaching the old one because this ordering allows the player
            // to swap directly from one surface to another, without transitioning through a state where no
            // surface is attached. This is significantly more efficient and achieves a more seamless
            // transition when using platform provided video decoders.
            if (newPlayerView != null) {
                newPlayerView.setPlayer(player)
            }
            if (oldPlayerView != null) {
                oldPlayerView.setPlayer(null)
            }
        }

        private const val DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS = 200
        private const val MAX_UPDATE_INTERVAL_MS = 1000
    }

    private val gestureDetector: GestureDetectorCompat
    private lateinit var gestureListener:KinescopeGestureListener

    private inner class KinescopeGestureListener(private val rootView: View) : GestureDetector.SimpleOnGestureListener() {
        private fun isForward(event:MotionEvent):Boolean {
            return event.x > (rootView.width / 2)
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            Log.d("KinescopeSDK", "double tap")
            return super.onDoubleTap(e)
        }

        override fun onDoubleTapEvent(e: MotionEvent): Boolean {
            Log.d("KinescopeSDK", "double tap event, isForward=${isForward(e)}")
            if (isForward(e)) seekView?.showForwardView(e) else seekView?.showBackView(e)
            return super.onDoubleTapEvent(e)
        }

        override fun onDown(e: MotionEvent?): Boolean {
            Log.d("KinescopeSDK", "tap down")
            return super.onDown(e)
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            Log.d("KinescopeSDK", "single tap confirmed")
            toggleControlUI()
            return false;
        }

        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            Log.d("KinescopeSDK", "single tap up")
            return super.onSingleTapUp(e)
        }
    }

    var onFullscreenButtonCallback:(()-> Unit)? = null

    private val formatBuilder: StringBuilder = StringBuilder()
    private val formatter = java.util.Formatter(formatBuilder, java.util.Locale.getDefault())

    private var kinescopePlayer: KinescopePlayer? = null
    private var exoPlayerView: StyledPlayerView? = null
    private var controlView:FrameLayout? = null
    private var seekView:KinesopeSeekView? = null
    private var bufferingView:View? = null
    private var positionView:TextView? = null
    private var durationView:TextView? = null
    private var timeBar:TimeBar? = null
    private var playPauseButton: View? = null
    private var optionsButton: View? = null
    private var fullscreenButton: View? = null

    private var titleView:TextView? = null
    private var authorView:TextView? = null

    private var settingsWindow: PopupWindow? = null
    private var settingsview: RecyclerView? = null
    private var settingsAdapter: KinescopeSettingsAdapter? = null
    private var playbackSpeedAdapter: KinescopeSettingsAdapter? = null
    private val settingsWindowMargin = resources.getDimensionPixelSize(com.google.android.exoplayer2.ui.R.dimen.exo_settings_offset)

    private var scrubbing = false
    private var window = Timeline.Window()

    private val showBuffering = 1

    private var currentWindowOffset: Long = 0
    private val timeBarMinUpdateIntervalMs = DEFAULT_TIME_BAR_MIN_UPDATE_INTERVAL_MS

    private val updateProgressRunnable = Runnable { updateProgress() }

    private var playbackSpeedOption:String = "normal"
    private val onPlaybackSpeedOptionsCallback = object: (String) -> Unit {
        override fun invoke(speed: String) {
            when (speed) {
                "normal" -> {
                    playbackSpeedOption = "normal"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(1f)
                }
                "0.25" -> {
                    playbackSpeedOption = "0.25"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(0.25f)
                }
                "0.5" -> {
                    playbackSpeedOption = "0.5"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(0.5f)
                }
                "0.75" -> {
                    playbackSpeedOption = "0.75"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(0.75f)
                }
                "1.25" -> {
                    playbackSpeedOption = "1.25"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(1.25f)
                }
                "1.5" -> {
                    playbackSpeedOption = "1.5"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(1.5f)
                }
                "1.75" -> {
                    playbackSpeedOption = "1.75"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(1.75f)
                }
                "2" -> {
                    playbackSpeedOption = "2"
                    kinescopePlayer?.exoPlayer?.setPlaybackSpeed(2f)
                }
            }
            settingsWindow?.dismiss()
        }

    }

    private val progressUpdateListener =
        StyledPlayerControlView.ProgressUpdateListener {
                position, bufferedPosition ->
        }

    private var componentListener = object:
        Player.Listener,
        OnClickListener,
        TimeBar.OnScrubListener,
    PopupWindow.OnDismissListener {
        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
            if (events.containsAny(
                    Player.EVENT_PLAYBACK_STATE_CHANGED,
                    Player.EVENT_PLAY_WHEN_READY_CHANGED
                )
            ) {
                //updatePlayPauseButton()
                updateAll()
            }
        }

        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
            super.onPlayWhenReadyChanged(playWhenReady, reason)
            updateBuffering()
            //updateControllerVisibility();
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            updateBuffering()
            //updateControllerVisibility()
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
            else if (fullscreenButton === view) {
                onFullscreenButtonCallback?.invoke()
            }
            else if (optionsButton === view) {
                displaySettingsWindow(playbackSpeedAdapter!!)
            }
        }

        override fun onDismiss() {

        }
    }


    init {
        inflate(context, R.layout.view_kinesope_player, this)
        exoPlayerView = findViewById(R.id.view_exoplayer)
        bufferingView = findViewById(R.id.view_buffering)
        bufferingView?.isVisible = false

        gestureListener = KinescopeGestureListener(rootView)
        gestureDetector = GestureDetectorCompat(context, gestureListener)

        controlView = findViewById(R.id.view_control)

        seekView = findViewById(R.id.kinescope_seek_view)

        timeBar = controlView?.findViewById<KinescopeTimeBar>(R.id.kinescope_progress)
        positionView = controlView?.findViewById(R.id.kinescope_position)
        durationView = controlView?.findViewById(R.id.kinescope_duration)
        playPauseButton = controlView?.findViewById(R.id.kinescope_play_pause)
        optionsButton = controlView?.findViewById(R.id.kinescope_settings)
        fullscreenButton = controlView?.findViewById(R.id.kinescope_fullscreen)
        titleView = controlView?.findViewById(R.id.kinescope_title)
        authorView = controlView?.findViewById(R.id.kinescope_author)

        settingsview = LayoutInflater.from(context).inflate(R.layout.view_options_list, null) as RecyclerView?
        settingsAdapter = KinescopeSettingsAdapter(arrayOf("Playback speed", "Quality"), null, null)
        playbackSpeedAdapter = KinescopeSettingsAdapter(resources.getStringArray(R.array.menu_playback_speed), playbackSpeedOption, onPlaybackSpeedOptionsCallback)
        settingsview?.adapter = playbackSpeedAdapter
        settingsview?.layoutManager = LinearLayoutManager(this@KinescopePlayerView.context, LinearLayoutManager.VERTICAL, false)
        settingsWindow = PopupWindow(
            settingsview,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            true
        )
        settingsWindow?.setOnDismissListener(componentListener)

        setUIlisteners()
    }

    fun setPlayer(kinescopePlayer: KinescopePlayer?) {
        Assertions.checkState(Looper.myLooper() == Looper.getMainLooper())
        if (this.kinescopePlayer === kinescopePlayer) return
        this.kinescopePlayer?.exoPlayer?.removeListener(componentListener)
        this.kinescopePlayer = kinescopePlayer
        kinescopePlayer?.exoPlayer?.addListener(componentListener)
        exoPlayerView?.player = kinescopePlayer?.exoPlayer
        updateAll()
    }

    private fun setPlaybackSpeed(speed:Float) {
        kinescopePlayer?.setPlaybackSpeed(speed)
    }

    private fun getVideo():KinescopeVideo? = kinescopePlayer?.getVideo()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        /*isAttachedToWindow = true
        if (isFullyVisible()) {
            controlViewLayoutManager.resetHideCallbacks()
        }*/
        updateAll()
    }

    fun setCustomControllerLayoutID(value:Int) {

    }

    private fun updateSettingsWindowSize() {
        settingsview!!.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
        val maxWidth: Int = width - settingsWindowMargin * 2
        val itemWidth: Int = settingsview!!.measuredWidth
        val width = Math.min(itemWidth, maxWidth)
        settingsWindow!!.width = width
        val maxHeight: Int = height - settingsWindowMargin * 2
        val totalHeight: Int = settingsview!!.measuredHeight
        val height = Math.min(maxHeight, totalHeight)
        settingsWindow!!.height = height
    }

    private fun displaySettingsWindow(adapter: RecyclerView.Adapter<*>) {
        settingsview?.adapter = adapter
        updateSettingsWindowSize()
        settingsWindow!!.dismiss()
        val xoff: Int = width - settingsWindow!!.width - settingsWindowMargin
        val yoff: Int = -settingsWindow!!.height - optionsButton!!.height - settingsWindowMargin
        settingsWindow!!.showAsDropDown(optionsButton, xoff, yoff)
    }

    private fun updateBuffering() {
        if (bufferingView != null) {
            val showBufferingSpinner =
                kinescopePlayer?.exoPlayer != null && kinescopePlayer!!.exoPlayer!!.playbackState == Player.STATE_BUFFERING && (showBuffering == StyledPlayerView.SHOW_BUFFERING_ALWAYS
                        || showBuffering == StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING && kinescopePlayer!!.exoPlayer!!.playWhenReady)
            bufferingView!!.isVisible = showBufferingSpinner

            val view = bufferingView?.findViewById<ProgressBar>(R.id.kinescope_buffering)
            if (showBufferingSpinner) {
                view?.animateRotation()
            }
            else {
                view?.clearAnimation()
            }
        }
    }

    private fun updateAll() {
        updatePlayPauseButton()
        /*
        updateNavigation()
        updateRepeatModeButton()
        updatePlaybackSpeedList()*/
        updateBuffering()
        updateTimeline()
        updateTitles()
    }

    private fun updateTitles() {
        if (getVideo() == null) return
        titleView?.text = getVideo()!!.title
        authorView?.text = getVideo()!!.subtitle
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
        return kinescopePlayer?.exoPlayer != null && kinescopePlayer!!.exoPlayer!!.playbackState != Player.STATE_ENDED && kinescopePlayer!!.exoPlayer!!.playbackState != Player.STATE_IDLE && kinescopePlayer!!.exoPlayer!!.playWhenReady
    }

    private fun shouldShowReplayButton(): Boolean {
        return kinescopePlayer?.exoPlayer != null && kinescopePlayer!!.exoPlayer!!.playbackState == Player.STATE_ENDED
    }

    private fun setUIlisteners() {
        controlView?.isVisible = false
        this.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            return@setOnTouchListener true
        }

        timeBar?.addListener(componentListener)
        playPauseButton?.setOnClickListener(componentListener)
        optionsButton?.setOnClickListener(componentListener)
        fullscreenButton?.setOnClickListener(componentListener)
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

    private fun toggleControlUI() {
        controlView!!.isVisible = !controlView!!.isVisible
        updateAll()
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