package io.kinescope.sdk.network

import io.kinescope.sdk.logger.KinescopeLogger
import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import io.kinescope.sdk.models.common.KinescopeResponse
import io.kinescope.sdk.models.videos.KinescopeVideo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
object Repository {
    fun getVideo(value: String, callback: GetVideoCallback) {
        RetrofitBuilder.getVideoApi().getVideo(value).enqueue(object : Callback<KinescopeResponse<KinescopeVideo>> {
            override fun onResponse(
                call: Call<KinescopeResponse<KinescopeVideo>>,
                response: Response<KinescopeResponse<KinescopeVideo>>
            ) {
                callback.onResponse(response.body()!!.data)
                KinescopeLogger.log("SUCCESS")
            }

            override fun onFailure(call: Call<KinescopeResponse<KinescopeVideo>>, t: Throwable) {
                KinescopeLogger.log("FAILURE")
                callback.onFailure()
            }

        })
    }

    fun getAll(callback: GetAllVideosCallback) {
        RetrofitBuilder.getVideoApi().getAll().enqueue(object : Callback<KinescopeAllVideosResponse> {
            override fun onResponse(
                call: Call<KinescopeAllVideosResponse>,
                response: Response<KinescopeAllVideosResponse>
            ) {
                callback.onResponse(response.body()!!)
                KinescopeLogger.log("SUCCESS")
            }

            override fun onFailure(call: Call<KinescopeAllVideosResponse>, t: Throwable) {
                callback.onFailure()
                KinescopeLogger.log("FAILURE")
            }
        })
    }

    interface GetAllVideosCallback {
        fun onResponse(value:KinescopeAllVideosResponse)
        fun onFailure()
    }

    interface GetVideoCallback {
        fun onResponse(value:KinescopeVideo)
        fun onFailure()
    }




}*/
