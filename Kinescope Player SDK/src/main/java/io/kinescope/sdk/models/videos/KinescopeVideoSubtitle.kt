package io.kinescope.sdk.models.videos

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideoSubtitle (
        val id: String,
        val description : String,
        val language : String,
        val url : String,
        ):Serializable