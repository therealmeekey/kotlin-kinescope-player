package io.kinescope.sdk.models.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopePagination (
    @Json(name = "page") val page: Int?,
    @Json(name = "per_page") val perPage: Int?,
    @Json(name = "total") val total: Int?,
        ):Serializable