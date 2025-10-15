package io.kinescope.sdk.player

import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.decoder.CryptoConfig
import androidx.media3.exoplayer.drm.DrmSession
import androidx.media3.exoplayer.drm.DrmSessionEventListener
import java.util.UUID

/**
 * Clear DRM Session - указывает что контент не зашифрован
 */
@UnstableApi
class ClearDrmSession : DrmSession {
    
    override fun getState(): Int {
        // Возвращаем состояние "opened" - сессия активна
        return DrmSession.STATE_OPENED
    }

    override fun playClearSamplesWithoutKeys(): Boolean {
        // Разрешаем воспроизведение незашифрованных семплов
        return true
    }

    override fun getError(): DrmSession.DrmSessionException? {
        // Нет ошибок
        return null
    }

    override fun getSchemeUuid(): UUID {
        // Возвращаем UUID для "clear key" (незашифрованный контент)
        return C.UUID_NIL
    }

    override fun getCryptoConfig(): CryptoConfig? {
        // Нет криптографии - контент незашифрован
        return null
    }

    override fun queryKeyStatus(): MutableMap<String, String>? {
        // Нет ключей
        return null
    }

    override fun getOfflineLicenseKeySetId(): ByteArray? {
        // Нет оффлайн лицензии
        return null
    }

    override fun requiresSecureDecoder(mimeType: String): Boolean {
        // Не требуется secure декодер
        return false
    }

    override fun acquire(eventDispatcher: DrmSessionEventListener.EventDispatcher?) {
        // Захват сессии - ничего не делаем
        android.util.Log.d("KinescopeSDK", "ClearDrmSession.acquire() - clear content, no DRM needed")
    }

    override fun release(eventDispatcher: DrmSessionEventListener.EventDispatcher?) {
        // Освобождение сессии - ничего не делаем
        android.util.Log.d("KinescopeSDK", "ClearDrmSession.release() - clear content released")
    }
}


