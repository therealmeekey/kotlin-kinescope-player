package io.kinescope.sdk.player

import android.content.Context
import android.net.Uri
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.drm.DefaultDrmSessionManager
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.FrameworkMediaDrm
import androidx.media3.exoplayer.dash.DashChunkSource
import androidx.media3.exoplayer.dash.DashMediaSource
import androidx.media3.exoplayer.dash.DefaultDashChunkSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.trackselection.AdaptiveTrackSelection
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.upstream.DefaultBandwidthMeter
import com.google.common.collect.ImmutableList
import io.kinescope.sdk.api.KinescopeFetch
import io.kinescope.sdk.logger.KinescopeLogger
import io.kinescope.sdk.logger.KinescopeLoggerLevel
import io.kinescope.sdk.models.videos.KinescopeVideo
import io.kinescope.sdk.network.FetchBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@UnstableApi
class KinescopeVideoPlayer(
    val context: Context,
    val kinescopePlayerOptions: KinescopePlayerOptions
) {
    constructor(context: Context) : this(context, KinescopePlayerOptions())

    var exoPlayer: ExoPlayer? = null
    var onSourceChanged: ((source: String, metricUrl: String?) -> Unit)? = null

    private val USER_AGENT = "KinescopeAndroidVideoKotlin"
    private var currentKinescopeVideo: KinescopeVideo? = null
    private var fetch: KinescopeFetch

    init {
        // Создаем ExoPlayer с отключенным DRM
        // Это предотвращает UnsupportedDrmException когда в HLS манифесте есть #EXT-X-KEY теги
        // Настраиваем буферы для live streaming
        val loadControl = androidx.media3.exoplayer.DefaultLoadControl.Builder()
            .setBufferDurationsMs(
                2000,  // min buffer (2 секунды) - минимум для старта
                30000, // max buffer (30 секунд)
                1000,  // playback buffer (1 секунда) - очень низкий порог
                2000   // playback rebuffer (2 секунды)
            )
            .setTargetBufferBytes(-1) // unlimited
            .setPrioritizeTimeOverSizeThresholds(true)
            .build()
        
        android.util.Log.d("KinescopeSDK", "LoadControl configured: min=2s, max=30s, playback=1s, rebuffer=2s")
        
        exoPlayer = ExoPlayer.Builder(context)
            .setTrackSelector(DefaultTrackSelector(context, AdaptiveTrackSelection.Factory()))
            .setLoadControl(loadControl)
            .setSeekBackIncrementMs(10000)
            .setSeekForwardIncrementMs(10000)
            .build()

        fetch = FetchBuilder.getKinescopeFetch(kinescopePlayerOptions.referer)
    }

    private fun getDashMediaSource(videoBuilder: MediaItem.Builder): DashMediaSource {
        val headers: MutableMap<String, String> = HashMap()
        headers["Origin"] = "*/*"
        headers["x-drm-type"] = "widevine"
        headers["Referer"] = kinescopePlayerOptions.referer

        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent(USER_AGENT)
            .setDefaultRequestProperties(headers)
            .setTransferListener(
                DefaultBandwidthMeter.Builder(context)
                    .setResetOnNetworkTypeChange(false)
                    .build()
            )

        val dashChunkSourceFactory: DashChunkSource.Factory = DefaultDashChunkSource.Factory(
            defaultHttpDataSourceFactory
        )

        return DashMediaSource.Factory(dashChunkSourceFactory, defaultHttpDataSourceFactory)
            .setManifestParser(KinescopeDashManifestParser())
            .setLoadErrorHandlingPolicy(KinescopeErrorHandlingPolicy())
            .createMediaSource(
                videoBuilder
                    .setDrmConfiguration(
                        MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                            .build()
                    )
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .setTag(null)
                    .build()
            )
    }

    private fun setVideo(kinescopeVideo: KinescopeVideo) {
        val source: String

        val mediaSource = when {
            kinescopeVideo.dashLink.isNullOrEmpty().not() -> {
                val videoBuilder: MediaItem.Builder = MediaItem.Builder()
                    .setUri(Uri.parse(kinescopeVideo.dashLink))

                if (getShowSubtitles() && kinescopeVideo.subtitles.isNotEmpty()) {
                    val subtitle: MediaItem.SubtitleConfiguration =
                        MediaItem.SubtitleConfiguration.Builder(Uri.parse(kinescopeVideo.subtitles.first().url))
                            .setMimeType(MimeTypes.TEXT_VTT)
                            .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                            .build()

                    videoBuilder.setSubtitleConfigurations(ImmutableList.of(subtitle))
                }

                source = kinescopeVideo.dashLink.orEmpty()
                getDashMediaSource(videoBuilder)
            }

            kinescopeVideo.hlsLink.isNullOrEmpty().not() -> {
                source = kinescopeVideo.hlsLink.orEmpty()
                
                // Добавляем Referer header для авторизации
                val headers: MutableMap<String, String> = HashMap()
                headers["Referer"] = kinescopePlayerOptions.referer
                
                // Определяем это live stream по URL (содержит "/on-air/")
                val isLiveStream = kinescopeVideo.hlsLink.orEmpty().contains("/on-air/")
                
                android.util.Log.d("KinescopeSDK", "Loading HLS: ${kinescopeVideo.hlsLink}")
                android.util.Log.d("KinescopeSDK", "Detected as LIVE stream: $isLiveStream")
                
                val baseDataSourceFactory = DefaultHttpDataSource.Factory()
                    .setUserAgent(USER_AGENT)
                    .setDefaultRequestProperties(headers)
                
                // НЕ используем NoDrmHttpDataSource - контент действительно зашифрован!
                val dataSourceFactory = baseDataSourceFactory
                
                val mediaItemBuilder = MediaItem.Builder()
                    .setUri(kinescopeVideo.hlsLink.orEmpty())
                
                // Добавляем LiveConfiguration только для live stream
                if (isLiveStream) {
                    mediaItemBuilder.setLiveConfiguration(
                        MediaItem.LiveConfiguration.Builder()
                            .setTargetOffsetMs(5000)
                            .setMinOffsetMs(2000)
                            .setMaxOffsetMs(10000)
                            .setMinPlaybackSpeed(0.95f)
                            .setMaxPlaybackSpeed(1.05f)
                            .build()
                    )
                    android.util.Log.d("KinescopeSDK", "MediaItem configured with LiveConfiguration")
                }
                
                val hlsFactory = HlsMediaSource.Factory(dataSourceFactory as androidx.media3.datasource.DataSource.Factory)
                    .setLoadErrorHandlingPolicy(KinescopeErrorHandlingPolicy())
                
                // DRM настраиваем ТОЛЬКО для VOD
                // Live stream с пустым DRM токеном игнорирует EXT-X-KEY теги автоматически
                if (!isLiveStream && kinescopeVideo.drm?.widevine?.licenseUrl != null) {
                    val drmCallback = KinescopeDrmCallback(kinescopeVideo.drm.widevine.licenseUrl.orEmpty())
                    val drmSessionManager = DefaultDrmSessionManager.Builder()
                        .setUuidAndExoMediaDrmProvider(
                            androidx.media3.common.C.WIDEVINE_UUID,
                            FrameworkMediaDrm.DEFAULT_PROVIDER
                        )
                        .build(drmCallback)
                    
                    android.util.Log.d("KinescopeSDK", "HLS VOD: Using Widevine DRM")
                    hlsFactory.setDrmSessionManagerProvider { drmSessionManager }
                } else if (isLiveStream) {
                    android.util.Log.d("KinescopeSDK", "HLS LIVE: Skipping DRM configuration (will ignore EXT-X-KEY tags)")
                }
                
                hlsFactory.createMediaSource(mediaItemBuilder.build())
            }

            else -> return
        }

        currentKinescopeVideo = kinescopeVideo
        onSourceChanged?.invoke(
            source,
            kinescopeVideo.sdk?.metricUrl
        )

        exoPlayer?.setMediaSource(mediaSource)
        exoPlayer?.prepare()
        android.util.Log.d("KinescopeSDK", "Before play: playWhenReady=${exoPlayer?.playWhenReady}, state=${exoPlayer?.playbackState}")
        exoPlayer?.playWhenReady = true
        exoPlayer?.play()
        android.util.Log.d("KinescopeSDK", "After play: playWhenReady=${exoPlayer?.playWhenReady}, state=${exoPlayer?.playbackState}")
    }

    private fun fetchUpdate() {
        fetch = FetchBuilder.getKinescopeFetch(kinescopePlayerOptions.referer)
    }

    fun getVideo(): KinescopeVideo? = currentKinescopeVideo

    fun loadVideo(
        videoId: String,
        onSuccess: ((KinescopeVideo?) -> Unit)? = null,
        onFailed: ((t: Throwable?) -> Unit)? = null,
    ) {
        fetch.getVideo(videoId).enqueue(object : Callback<KinescopeVideo> {
            override fun onResponse(
                call: Call<KinescopeVideo>,
                response: Response<KinescopeVideo>
            ) {
                if (response.isSuccessful) {
                    val video = response.body()!!
                    
                    // Логируем DRM информацию для отладки
                    android.util.Log.d("KinescopeSDK", "=== VIDEO DETAILS ===")
                    android.util.Log.d("KinescopeSDK", "Title: ${video.title}")
                    android.util.Log.d("KinescopeSDK", "Type: ${video.type}")
                    android.util.Log.d("KinescopeSDK", "IsLive: ${video.isLive}")
                    android.util.Log.d("KinescopeSDK", "HLS link: ${video.hlsLink}")
                    android.util.Log.d("KinescopeSDK", "DASH link: ${video.dashLink}")
                    android.util.Log.d("KinescopeSDK", "Live info: ${video.live}")
                    android.util.Log.d("KinescopeSDK", "DRM data: ${video.drm}")
                    android.util.Log.d("KinescopeSDK", "Widevine license URL: ${video.drm?.widevine?.licenseUrl}")
                    android.util.Log.d("KinescopeSDK", "FairPlay license URL: ${video.drm?.fairplay?.licenseUrl}")
                    android.util.Log.d("KinescopeSDK", "==================")
                    
                    setVideo(video)
                    onSuccess?.invoke(video)

                    if (onSuccess != null) {
                        onSuccess(video)
                    };
                } else {
                    KinescopeLogger.log(
                        KinescopeLoggerLevel.NETWORK,
                        "LoadVideo isSuccessful false"
                    )
                    onFailed?.invoke(null)
                }
            }

            override fun onFailure(call: Call<KinescopeVideo>, t: Throwable) {
                if (onFailed != null) {
                    onFailed(t)
                };
                KinescopeLogger.log(
                    KinescopeLoggerLevel.NETWORK,
                    "LoadVideo failed: $t.message.toString()"
                )
            }
        })
    }

    fun play() {
        exoPlayer?.play()
        KinescopeLogger.log(KinescopeLoggerLevel.PLAYER, "Start playing")
    }

    fun pause() {
        exoPlayer?.pause()
        KinescopeLogger.log(KinescopeLoggerLevel.PLAYER, "Pause playing")
    }

    fun stop() {
        exoPlayer?.stop()
        KinescopeLogger.log(KinescopeLoggerLevel.PLAYER, "Stop playing")
    }

    fun seekTo(toMilliSeconds: Long) {
        exoPlayer?.seekTo(exoPlayer!!.contentPosition + toMilliSeconds)
        KinescopeLogger.log(KinescopeLoggerLevel.PLAYER, "seek to ${toMilliSeconds / 1000} seconds")
    }

    fun moveForward() {
        exoPlayer?.seekForward()
        KinescopeLogger.log(
            KinescopeLoggerLevel.PLAYER,
            "Moved forward to ${exoPlayer!!.seekParameters.toleranceAfterUs}"
        )
    }

    fun moveBack() {
        exoPlayer?.seekBack()
        KinescopeLogger.log(
            KinescopeLoggerLevel.PLAYER,
            "Moved back to ${exoPlayer!!.seekParameters.toleranceBeforeUs}"
        )
    }

    fun setReferer(value: String) {
        kinescopePlayerOptions.referer = value
        fetchUpdate();
        KinescopeLogger.log(KinescopeLoggerLevel.PLAYER, "Referer $value")
    }

    fun setPlaybackSpeed(speed: Float) {
        exoPlayer?.setPlaybackSpeed(speed)
        KinescopeLogger.log(KinescopeLoggerLevel.PLAYER, "Playback speed changed to $speed")
    }

    fun setShowSubtitles(value: Boolean) {
        kinescopePlayerOptions.showSubtitlesButton = value
    }

    fun setShowOptions(value: Boolean) {
        kinescopePlayerOptions.showOptionsButton = value
    }

    fun getShowSubtitles(): Boolean {
        return kinescopePlayerOptions.showSubtitlesButton
    }

    fun setShowFullscreen(value: Boolean) {
        kinescopePlayerOptions.showFullscreenButton = value
    }
}