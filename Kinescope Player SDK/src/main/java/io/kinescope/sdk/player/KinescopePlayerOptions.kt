package io.kinescope.sdk.player

data class KinescopePlayerOptions (
    var showFullscreenButton:Boolean = true,
    var showOptions:Boolean = true,
    var showSubtitles:Boolean = false,
    var showQualityButton:Boolean = false,
    var showSeekBar:Boolean = true,
    var showDuration:Boolean = true,
    var showAttachments:Boolean = false
        )