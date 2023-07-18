package io.kinescope.sdk.models.videos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideoAsset (
        @Json(name = "id") val id:String,
        @Json(name = "video_id") val videoId:String,
        @Json(name = "original_name") val originalName:String,
        @Json(name = "file_size") val fileSize:String,
        @Json(name = "filetype") val filetype:String,
        @Json(name = "quality") val quality:String,
        @Json(name = "resolution") val resolution:String,
        @Json(name = "created_at") val createdAt:String,
        @Json(name = "updated_at") val updatedAt:String?,
        @Json(name = "url") val url:String,
        @Json(name = "download_link") val downloadLink:String,
        @Json(name = "md5") val md5:String?,
        ):Serializable