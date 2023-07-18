package io.kinescope.sdk.api

import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import io.kinescope.sdk.models.common.KinescopeVideoResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface KinescopeApi {
    @GET("v1/videos/")
    suspend fun getAll():KinescopeAllVideosResponse

    @GET("v1/videos/{video_id}")
    suspend fun getVideo(@Path("video_id") videoId:String):KinescopeVideoResponse
}
