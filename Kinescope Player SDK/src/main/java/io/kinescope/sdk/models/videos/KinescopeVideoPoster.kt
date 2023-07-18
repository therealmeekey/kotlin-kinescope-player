package io.kinescope.sdk.models.videos

import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideoPoster (
        val id:String,
        val original:String,
        val md:String,
        val sm:String,
        val xs:String
        ):Serializable