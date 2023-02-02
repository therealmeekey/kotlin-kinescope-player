package io.kinescope.sdk.models.videos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideo (
    @Json(name = "id") val id:String,
    @Json(name = "project_id")  val projectId:String,
    @Json(name = "folder_id")  val folderId:String?,
    @Json(name = "version")  val version:Int,
    @Json(name = "title")  val title: String,
    @Json(name = "subtitle")  val subtitle: String,
    @Json(name = "description") val description: String,
    @Json(name = "status") val status: String,
    @Json(name = "progress") val progress: Int,
    @Json(name = "duration") val duration: Float,
    @Json(name = "assets") val assets:List<KinescopeVideoAsset>,
    @Json(name = "chapters") val chapters: KinescopeVideoChapter,
    @Json(name = "poster") val poster: KinescopeVideoPoster?,
    @Json(name = "additional_materials") val additionalMaterials: List<KinescopeVideoAdditionalMaterial>,
    @Json(name = "additional_materials_enabled") val additionalMaterialsEnabled:Boolean,
    @Json(name = "subtitles") val subtitles: List<KinescopeVideoSubtitle>,
    @Json(name = "subtitles_enabled") val subtitlesEnabled: Boolean,
    @Json(name = "hls_link") val hlsLink:String,
    @Json(name = "play_link") val playLink:String
    ): Serializable