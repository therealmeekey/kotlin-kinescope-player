package io.kinescope.sdk.api

import io.kinescope.sdk.analytics.proto.Native
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface KinescopeAnalyticsApi {
    @POST("/player-native")
    fun sendEvent(@Body body: Native): Call<Void>
}