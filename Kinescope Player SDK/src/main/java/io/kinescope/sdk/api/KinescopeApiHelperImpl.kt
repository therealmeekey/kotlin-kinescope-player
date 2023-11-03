package io.kinescope.sdk.api

import kotlinx.coroutines.flow.flow

class KinescopeApiHelperImpl (private val  apiService: KinescopeApi) : KinescopeApiHelper {
    override fun getAllVideos() = flow {
        emit(apiService.getAll())
    }
}