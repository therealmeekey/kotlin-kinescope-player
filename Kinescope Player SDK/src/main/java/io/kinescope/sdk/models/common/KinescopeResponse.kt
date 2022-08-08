package io.kinescope.sdk.models.common

import io.kinescope.sdk.models.pagination.KinescopeMetaData
import io.kinescope.sdk.models.videos.KinescopeVideo
import java.io.Serializable

data class KinescopeResponse<A> (
    val data:A
        ):Serializable

data class KinescopeMetaResponse<A,B> (
    val data:A,
    val meta:B
):Serializable

typealias KinescopeAllVideosResponse = KinescopeMetaResponse<List<KinescopeVideo>,KinescopeMetaData>
