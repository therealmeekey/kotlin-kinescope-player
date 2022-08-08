package io.kinescope.sdk.network

import io.kinescope.sdk.logger.KinescopeLogger
import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import io.kinescope.sdk.models.common.KinescopeMetaResponse
import io.kinescope.sdk.models.pagination.KinescopeMetaData
import io.kinescope.sdk.models.videos.KinescopeVideo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Repository {
    fun getVideo(value:String) {
        NetworkModule.getVideoApi().getVideo(value).enqueue(object : Callback<KinescopeVideo> {
            override fun onResponse(
                call: Call<KinescopeVideo>,
                response: Response<KinescopeVideo>
            ) {
                KinescopeLogger.log("SUCCESS")
            }

            override fun onFailure(call: Call<KinescopeVideo>, t: Throwable) {
                KinescopeLogger.log("FAILURE")
            }

        })
    }

    fun getAll() {
        NetworkModule.getVideoApi().getAll().enqueue(object : Callback<KinescopeAllVideosResponse> {
            override fun onResponse(
                call: Call<KinescopeAllVideosResponse>,
                response: Response<KinescopeAllVideosResponse>
            ) {
                KinescopeLogger.log("SUCCESS")
            }

            override fun onFailure(call: Call<KinescopeAllVideosResponse>, t: Throwable) {
                KinescopeLogger.log("FAILURE")
            }
        })
    }




}