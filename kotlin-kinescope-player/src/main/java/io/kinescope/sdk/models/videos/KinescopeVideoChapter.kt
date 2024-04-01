package io.kinescope.sdk.models.videos

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideoChapter (
        val items:List<KinescopeVideoChapterItem>?
        ): Serializable