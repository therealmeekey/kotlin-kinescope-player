package io.kinescope.sdk.models.videos

import io.kinescope.sdk.models.pagination.KinescopeMetaData
import java.io.Serializable

data class AllVideosResponse (
    val meta: KinescopeMetaData,
    val data: ArrayList<KinescopeVideo>,
        ):Serializable
