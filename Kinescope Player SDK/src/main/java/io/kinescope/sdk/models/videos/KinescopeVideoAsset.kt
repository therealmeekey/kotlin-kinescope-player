package io.kinescope.sdk.models.videos

import java.io.Serializable

data class KinescopeVideoAsset (
        val id:String,
        val videoId:String,
        val originalName:String,
        val fileSize:String,
        val filetype:String,
        val quality:String,
        val resolution:String,
        val createdAt:String,
        val updatedAt:String,
        val url:String,
        ):Serializable