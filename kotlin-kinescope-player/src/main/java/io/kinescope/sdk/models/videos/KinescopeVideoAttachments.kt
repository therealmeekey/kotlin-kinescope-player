package io.kinescope.sdk.models.videos

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
class KinescopeVideoAttachments (
        val id: String,
        val title: String,
        val url: String,
        val filetype: String,
        val filename: String,
        val size: Int,
        ): Serializable