package io.kinescope.sdk.models.videos

import java.io.Serializable

data class KinescopeVideo (
    val id:String,
    val project_id:String,
    val folder_id:String,
    val version:Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val status: String,
    val progress: Int,
    val duration: Float,
    val assets:ArrayList<KinescopeVideoAsset>,
    val chapters: KinescopeVideoChapter,
    val poster: KinescopeVideoPoster?,
    val additionalMaterials: ArrayList<KinescopeVideoAdditionalMaterial>,
    val subtitles: ArrayList<KinescopeVideoSubtitle>,
    val hls_link:String
    ): Serializable