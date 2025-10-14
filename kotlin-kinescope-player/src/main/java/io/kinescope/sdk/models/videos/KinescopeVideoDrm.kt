package io.kinescope.sdk.models.videos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideoDrm(
    @Json(name = "fairplay") val fairplay: KinescopeVideoDrmFairplay?,
    @Json(name = "widevine") val widevine: KinescopeVideoDrmWidevine?
) : Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideoDrmFairplay(
    @Json(name = "certificateUrl") val certificateUrl: String?,
    @Json(name = "licenseUrl") val licenseUrl: String?
) : Serializable

@JsonClass(generateAdapter = true)
data class KinescopeVideoDrmWidevine(
    @Json(name = "licenseUrl") val licenseUrl: String?
) : Serializable



