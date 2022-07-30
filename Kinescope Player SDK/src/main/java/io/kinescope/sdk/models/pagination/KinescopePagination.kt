package io.kinescope.sdk.models.pagination

import java.io.Serializable

data class KinescopePagination (
    val page: Int,
    val perPage: Int,
    val total: Int,
        ):Serializable