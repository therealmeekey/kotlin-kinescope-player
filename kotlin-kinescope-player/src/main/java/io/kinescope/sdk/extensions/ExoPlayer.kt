package io.kinescope.sdk.extensions

import androidx.media3.exoplayer.ExoPlayer
import io.kinescope.sdk.analytics.KinescopeAnalyticsArgs
import kotlin.math.roundToInt

fun ExoPlayer?.getAnalyticsArguments(
    volume: Int,
    isFullscreen: Boolean,
): KinescopeAnalyticsArgs {
    return this?.let {
        val isMuted = volume == 0

        KinescopeAnalyticsArgs(
            duration = (duration.toFloat() / 1000f).roundToInt(),
            rate = playbackParameters.speed,
            volume = volume,
            quality = videoSize.height.toString(),
            isMuted = isMuted,
            isFullScreen = isFullscreen,
            previewPosition = (bufferedPosition.toFloat() / 1000f).roundToInt(),
            currentPosition = (currentPosition.toFloat() / 1000f).roundToInt(),
        )
    } ?: KinescopeAnalyticsArgs()
}