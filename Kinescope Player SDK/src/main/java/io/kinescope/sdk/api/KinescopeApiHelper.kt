package io.kinescope.sdk.api

import io.kinescope.sdk.models.common.KinescopeAllVideosResponse
import io.kinescope.sdk.models.common.KinescopeResponse
import io.kinescope.sdk.models.videos.KinescopeVideo
import kotlinx.coroutines.flow.Flow

interface KinescopeApiHelper {
    fun getAllVideos(): Flow<KinescopeAllVideosResponse>
    fun getVideo(videoId:String): Flow<KinescopeResponse<KinescopeVideo>>
}