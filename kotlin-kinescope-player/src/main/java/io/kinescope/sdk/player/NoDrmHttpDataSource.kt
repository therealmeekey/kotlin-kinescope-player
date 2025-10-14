package io.kinescope.sdk.player

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.DefaultHttpDataSource
import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * Custom DataSource который удаляет DRM теги из HLS манифестов
 */
@UnstableApi
class NoDrmHttpDataSource(
    private val wrappedDataSource: DefaultHttpDataSource
) : DataSource {
    
    private var manifestBuffer: ByteArray? = null
    private var manifestStream: ByteArrayInputStream? = null
    private var isM3u8 = false
    private var currentUri: Uri? = null
    
    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        // Сохраняем URI для getUri()
        currentUri = dataSpec.uri
        
        // Проверяем, это HLS манифест?
        isM3u8 = dataSpec.uri.toString().contains(".m3u8")
        
        android.util.Log.d("KinescopeSDK", "NoDrmHttpDataSource.open() called for: ${dataSpec.uri}")
        android.util.Log.d("KinescopeSDK", "Is M3U8: $isM3u8")
        
        val bytesRead = wrappedDataSource.open(dataSpec)
        android.util.Log.d("KinescopeSDK", "Base DataSource bytesRead: $bytesRead")
        android.util.Log.d("KinescopeSDK", "Base DataSource URI after open: ${wrappedDataSource.uri}")
        
        // Если это HLS манифест, читаем и модифицируем
        if (isM3u8) {
            try {
                // Читаем весь манифест (HLS манифесты обычно маленькие, < 100KB)
                val buffer = ByteArray(1024 * 100) // 100KB буфер
                var totalRead = 0
                var read: Int
                
                android.util.Log.d("KinescopeSDK", "Starting to read HLS manifest...")
                
                while (wrappedDataSource.read(buffer, totalRead, buffer.size - totalRead).also { read = it } != -1) {
                    android.util.Log.d("KinescopeSDK", "Read $read bytes, total: ${totalRead + read}")
                    totalRead += read
                    if (totalRead >= buffer.size) {
                        android.util.Log.w("KinescopeSDK", "Buffer full! Manifest might be truncated")
                        break
                    }
                }
                
                android.util.Log.d("KinescopeSDK", "Finished reading HLS manifest. Total bytes: $totalRead")
                
                // Закрываем wrappedDataSource после чтения
                wrappedDataSource.close()
                
                if (totalRead == 0) {
                    android.util.Log.w("KinescopeSDK", "No data read from HLS manifest!")
                    return 0
                }
                
                val manifestText = String(buffer, 0, totalRead, Charsets.UTF_8)
                android.util.Log.d("KinescopeSDK", "Original HLS manifest:\n$manifestText")
                
                // Удаляем все #EXT-X-KEY строки
                val lines = manifestText.lines()
                var drmTagsRemoved = 0
                val cleanedManifest = lines.filterNot { 
                    if (it.trim().startsWith("#EXT-X-KEY:")) {
                        android.util.Log.d("KinescopeSDK", "Removed DRM tag: ${it.trim()}")
                        drmTagsRemoved++
                        true
                    } else {
                        false
                    }
                }.joinToString("\n")
                
                android.util.Log.d("KinescopeSDK", "Removed $drmTagsRemoved DRM tags from HLS manifest. New size: ${cleanedManifest.length} bytes")
                
                if (drmTagsRemoved > 0) {
                    android.util.Log.d("KinescopeSDK", "Cleaned HLS manifest:\n$cleanedManifest")
                }
                
                val content = cleanedManifest.toByteArray(Charsets.UTF_8)
                manifestBuffer = content
                manifestStream = ByteArrayInputStream(content)
                
                android.util.Log.d("KinescopeSDK", "✅ Returning MODIFIED manifest, size: ${content.size} bytes")
                return content.size.toLong()
            } catch (e: Exception) {
                android.util.Log.e("KinescopeSDK", "Error reading/modifying HLS manifest", e)
                try {
                    wrappedDataSource.close()
                } catch (closeEx: Exception) {
                    android.util.Log.e("KinescopeSDK", "Error closing wrappedDataSource", closeEx)
                }
                // Возвращаем 0 в случае ошибки
                return 0
            }
        }
        
        return bytesRead
    }
    
    @Throws(IOException::class)
    override fun read(target: ByteArray, offset: Int, length: Int): Int {
        return if (isM3u8 && manifestStream != null) {
            manifestStream!!.read(target, offset, length)
        } else {
            wrappedDataSource.read(target, offset, length)
        }
    }
    
    override fun getUri(): Uri? {
        // Возвращаем сохраненный URI (важно для манифестов, где wrappedDataSource уже закрыт)
        return currentUri ?: wrappedDataSource.uri
    }
    
    override fun getResponseHeaders(): Map<String, List<String>> {
        return wrappedDataSource.responseHeaders
    }
    
    @Throws(IOException::class)
    override fun close() {
        manifestStream?.close()
        manifestBuffer = null
        manifestStream = null
        currentUri = null
        
        // Закрываем wrappedDataSource только если это не манифест
        // (для манифестов мы уже закрыли его в open())
        if (!isM3u8) {
            wrappedDataSource.close()
        }
        
        isM3u8 = false
    }
    
    override fun addTransferListener(transferListener: androidx.media3.datasource.TransferListener) {
        wrappedDataSource.addTransferListener(transferListener)
    }
    
    class Factory(
        private val baseFactory: DefaultHttpDataSource.Factory
    ) : DataSource.Factory {
        override fun createDataSource(): DataSource {
            return NoDrmHttpDataSource(baseFactory.createDataSource() as DefaultHttpDataSource)
        }
    }
}

