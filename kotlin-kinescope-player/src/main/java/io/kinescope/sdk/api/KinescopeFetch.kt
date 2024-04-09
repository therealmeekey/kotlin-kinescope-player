package io.kinescope.sdk.api

import io.kinescope.sdk.models.videos.KinescopeVideo
import io.kinescope.sdk.utils.SDK_TYPE
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface KinescopeFetch {
    @GET("{video_id}.json")
    fun getVideo(
        @Path("video_id") videoId: String,
        @Query("sdk") sdk: String = SDK_TYPE
    ): Call<KinescopeVideo>
}
