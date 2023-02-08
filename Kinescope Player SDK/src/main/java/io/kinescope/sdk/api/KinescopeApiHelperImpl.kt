package io.kinescope.sdk.api

import io.kinescope.sdk.api.KinescopeApi
import io.kinescope.sdk.api.KinescopeApiHelper

import kotlinx.coroutines.flow.flow


class KinescopeApiHelperImpl (private val  apiService: KinescopeApi) : KinescopeApiHelper {
    override fun getAllVideos() = flow {
        emit(apiService.getAllVideos())
    }

    override fun getVideo(videoId: String) = flow {
        emit(apiService.getVideo(videoId))
    }
}