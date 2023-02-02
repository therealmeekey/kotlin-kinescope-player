package io.kinescope.sdk.models.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeOrder (
    @Json(name = "created_at") val createdAt: String?,
    @Json(name = "order") val order: String?
): Serializable