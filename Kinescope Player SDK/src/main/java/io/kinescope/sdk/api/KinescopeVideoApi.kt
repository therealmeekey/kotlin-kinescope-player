package io.kinescope.sdk.api

import io.kinescope.sdk.models.videos.AllVideosResponse
import io.kinescope.sdk.models.videos.KinescopeVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface KinescopeVideoApi {
    @GET("v1/videos/{video_id}")
    fun getVideo(@Path("id") id:String):Call<KinescopeVideo>

    @GET("v1/videos/")
    fun getAll():Call<AllVideosResponse>
}