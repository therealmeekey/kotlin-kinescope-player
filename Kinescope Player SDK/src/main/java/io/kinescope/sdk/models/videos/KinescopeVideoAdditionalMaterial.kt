package io.kinescope.sdk.models.videos

import java.io.Serializable

class KinescopeVideoAdditionalMaterial (
        val id: String,
        val title: String,
        val url: String,
        val filetype: String,
        val filename: String,
        val size: Int,
        ): Serializable