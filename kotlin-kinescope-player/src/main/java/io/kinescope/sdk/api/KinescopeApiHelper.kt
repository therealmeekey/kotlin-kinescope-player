package io.kinescope.sdk.api

import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import kotlinx.coroutines.flow.Flow

interface KinescopeApiHelper {
    fun getAllVideos(): Flow<KinescopeAllVideosResponse>
}