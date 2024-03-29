package io.kinescope.sdk.player

data class KinescopePlayerOptions (
    var showFullscreenButton:Boolean = true,
    var showOptionsButton:Boolean = true,
    var showSubtitlesButton:Boolean = false,
    //var showQualityButton:Boolean = false,
    var showSeekBar:Boolean = true,
    var showDuration:Boolean = true,
    var showAttachments:Boolean = false
        )