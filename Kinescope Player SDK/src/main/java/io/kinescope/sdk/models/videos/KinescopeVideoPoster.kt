package io.kinescope.sdk.models.videos

import java.io.Serializable

data class KinescopeVideoPoster (
        val id:String,
        val original:String,
        val md:String,
        val sm:String,
        val xs:String
        ):Serializable