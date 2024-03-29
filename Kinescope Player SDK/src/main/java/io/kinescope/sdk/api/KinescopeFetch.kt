package io.kinescope.sdk.api

import io.kinescope.sdk.models.videos.KinescopeVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface KinescopeFetch {
    @GET("{video_id}.json")
    fun getVideo(@Path("video_id") videoId:String): Call<KinescopeVideo>
}
