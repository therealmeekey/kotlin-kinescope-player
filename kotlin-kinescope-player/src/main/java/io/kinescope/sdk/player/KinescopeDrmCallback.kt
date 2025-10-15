package io.kinescope.sdk.player

import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.drm.ExoMediaDrm
import androidx.media3.exoplayer.drm.MediaDrmCallback
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * DRM Callback для Kinescope который делает запросы к license server
 */
@UnstableApi
class KinescopeDrmCallback(
    private val licenseUrl: String
) : MediaDrmCallback {

    override fun executeProvisionRequest(
        uuid: UUID,
        request: ExoMediaDrm.ProvisionRequest
    ): ByteArray {
        android.util.Log.d("KinescopeSDK", "KinescopeDrmCallback.executeProvisionRequest() - UUID: $uuid")
        // Provisioning обычно не требуется для Widevine L3
        return ByteArray(0)
    }

    @Throws(IOException::class)
    override fun executeKeyRequest(
        uuid: UUID,
        request: ExoMediaDrm.KeyRequest
    ): ByteArray {
        android.util.Log.d("KinescopeSDK", "KinescopeDrmCallback.executeKeyRequest()")
        android.util.Log.d("KinescopeSDK", "License URL: $licenseUrl")
        android.util.Log.d("KinescopeSDK", "Request data size: ${request.data.size}")
        
        val url = URL(licenseUrl)
        val connection = url.openConnection() as HttpURLConnection
        
        try {
            connection.requestMethod = "POST"
            connection.doOutput = true
            connection.doInput = true
            connection.setRequestProperty("Content-Type", "application/octet-stream")
            connection.setRequestProperty("User-Agent", "Kinescope Android SDK")
            
            android.util.Log.d("KinescopeSDK", "Sending license request to: $licenseUrl")
            
            // Отправляем license request
            connection.outputStream.use { outputStream ->
                outputStream.write(request.data)
                outputStream.flush()
            }
            
            val responseCode = connection.responseCode
            android.util.Log.d("KinescopeSDK", "License response code: $responseCode")
            
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val response = connection.inputStream.use { it.readBytes() }
                android.util.Log.d("KinescopeSDK", "✅ License received, size: ${response.size} bytes")
                return response
            } else {
                val errorMessage = try {
                    connection.errorStream?.use { it.readBytes()?.toString(Charsets.UTF_8) } ?: "No error message"
                } catch (e: Exception) {
                    "Failed to read error: ${e.message}"
                }
                android.util.Log.e("KinescopeSDK", "❌ License request failed: $responseCode - $errorMessage")
                throw IOException("License request failed with code $responseCode: $errorMessage")
            }
        } finally {
            connection.disconnect()
        }
    }
}


