package io.kinescope.demo.api

import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import kotlinx.coroutines.flow.Flow

interface KinescopeApiHelper {
    fun getAllVideosList(): Flow<KinescopeAllVideosResponse>
}