package io.kinescope.sdk.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.DrmSessionEventListener
import androidx.media3.exoplayer.drm.DrmSessionManager
import androidx.media3.exoplayer.drm.DrmSession

/**
 * Dummy DRM Session Manager который игнорирует все DRM запросы
 * Используется для воспроизведения незашифрованного контента, даже если в манифесте есть DRM теги
 */
@UnstableApi
class DummyDrmSessionManager : DrmSessionManager {
    
    override fun prepare() {
        // Ничего не делаем
        android.util.Log.d("KinescopeSDK", "DummyDrmSessionManager.prepare() - ignoring DRM")
    }

    override fun release() {
        // Ничего не делаем
        android.util.Log.d("KinescopeSDK", "DummyDrmSessionManager.release() - ignoring DRM")
    }

    override fun acquireSession(
        eventDispatcher: DrmSessionEventListener.EventDispatcher,
        format: androidx.media3.common.Format
    ): DrmSession? {
        android.util.Log.d("KinescopeSDK", "DummyDrmSessionManager.acquireSession() - returning null (no DRM)")
        // Возвращаем null - это означает что DRM не требуется
        return null
    }

    override fun getCryptoType(format: androidx.media3.common.Format): Int {
        android.util.Log.d("KinescopeSDK", "DummyDrmSessionManager.getCryptoType() - returning CRYPTO_TYPE_NONE")
        // Возвращаем CRYPTO_TYPE_NONE - контент не зашифрован
        return androidx.media3.common.C.CRYPTO_TYPE_NONE
    }
}
