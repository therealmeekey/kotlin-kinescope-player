package io.kinescope.sdk.models.videos

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class KinescopeSdk(
    @Json(name = "metric_url") val metricUrl: String?
)
