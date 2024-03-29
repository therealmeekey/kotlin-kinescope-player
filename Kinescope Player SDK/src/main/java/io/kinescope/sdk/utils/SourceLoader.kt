package io.kinescope.sdk.utils

import android.content.Context
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.source.SingleSampleMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.MimeTypes
import java.text.Format

class SourceLoader(context: Context) {
    /*// create the simple cache
    val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
    val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)
    val simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)


    // create http datasource
    val defaultBandwidthMeter = DefaultBandwidthMeter.Builder(context).build()
    val dataSourceFactory: DataSource.Factory = DefaultDataSourceFactory(
        context,
        DefaultHttpDataSourceFactory(
            System.getProperty("http.agent"),
            defaultBandwidthMeter
        )
    )

    fun getSubtitlesSource(uri:String): SingleSampleMediaSource {
        // create subtitle text format
        val textFormat = Format.createTextSampleFormat(
            null,
            MimeTypes.TEXT_VTT,
            C.SELECTION_FLAG_DEFAULT,
            null
        )

        return SingleSampleMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse(uri), textFormat, C.TIME_UNSET)
    }*/
}