package io.kinescope.sdk.api

import io.kinescope.sdk.analytics.proto.Native
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Url

interface KinescopeAnalyticsApi {
    @POST
    fun sendEvent(@Url url: String, @Body body: Native): Call<Void>
}