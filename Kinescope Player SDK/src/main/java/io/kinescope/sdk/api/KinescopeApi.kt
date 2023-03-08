package io.kinescope.sdk.api

import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import io.kinescope.sdk.models.common.KinescopeResponse
import io.kinescope.sdk.models.videos.KinescopeVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface KinescopeVideoApi {
    @GET("v1/videos/{video_id}")
    fun getVideo(@Path("video_id") videoId:String):Call<KinescopeResponse<KinescopeVideo>>

    @GET("v1/videos/")
    fun getAll():Call<KinescopeAllVideosResponse>


}

interface KinescopeApi {
    @GET("v1/videos/")
    suspend fun getAllVideos():KinescopeAllVideosResponse

    @GET("v1/videos/{video_id}")
    fun getVideo(@Path("video_id") videoId:String):KinescopeResponse<KinescopeVideo>
}
