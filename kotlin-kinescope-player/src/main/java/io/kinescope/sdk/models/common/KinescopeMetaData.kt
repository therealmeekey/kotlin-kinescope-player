package io.kinescope.sdk.models.common

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

@JsonClass(generateAdapter = true)
data class KinescopeMetaData (
    @Json(name = "pagination") val pagination: KinescopePagination?,
    @Json(name = "order") val order: KinescopePagination?
    ):Serializable