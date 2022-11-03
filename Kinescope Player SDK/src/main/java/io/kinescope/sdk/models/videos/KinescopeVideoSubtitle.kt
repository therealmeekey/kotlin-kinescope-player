package io.kinescope.sdk.models.videos

import java.io.Serializable

data class KinescopeVideoSubtitle (
        val id: String,
        val description : String,
        val language : String,
        val url : String,
        ):Serializable