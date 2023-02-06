package io.kinescope.demo.api

import io.kinescope.sdk.api.KinescopeApi

import kotlinx.coroutines.flow.flow


class KinescopeApiHelperImpl (private val  apiService: KinescopeApi) : KinescopeApiHelper {
    override fun getAllVideosList() = flow {
        emit(apiService.getAllVideosList())
    }
}