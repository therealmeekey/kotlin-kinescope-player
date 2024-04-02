package io.kinescope.sdk.extensions

import android.media.AudioManager

val AudioManager.currentVolumeInPercent: Int
    get() {
        val currentVolume = getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        return 100 * currentVolume / maxVolume
    }