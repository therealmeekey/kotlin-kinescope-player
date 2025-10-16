package io.kinescope.sdk.player

import android.os.Looper
import androidx.media3.common.Format
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.DrmSession
import androidx.media3.exoplayer.drm.DrmSessionEventListener
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.ExoMediaDrm

/**
 * DrmSessionManager который игнорирует все DRM запросы
 * Используется для live stream с пустым DRM токеном
 */
@UnstableApi
class IgnoreDrmSessionManager : DrmSessionManager {
    
    override fun prepare() {
        // No-op
    }
    
    override fun release() {
        // No-op
    }
    
    override fun getCryptoType(format: Format): Int {
        return androidx.media3.common.C.CRYPTO_TYPE_NONE
    }
    
    override fun acquireSession(
        eventDispatcher: DrmSessionEventListener.EventDispatcher?,
        format: Format
    ): DrmSession? {
        // Возвращаем null - нет DRM session
        return null
    }
    
    override fun setPlayer(playbackLooper: Looper, playerId: androidx.media3.exoplayer.analytics.PlayerId) {
        // No-op
    }
}

