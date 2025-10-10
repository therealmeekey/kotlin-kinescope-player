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
    
    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {
        // Проверяем, это HLS манифест?
        isM3u8 = dataSpec.uri.toString().contains(".m3u8")
        
        val bytesRead = wrappedDataSource.open(dataSpec)
        
        // Если это HLS манифест, читаем и модифицируем
        if (isM3u8) {
            val content = wrappedDataSource.uri?.let { uri ->
                // Читаем весь манифест (HLS манифесты обычно маленькие, < 100KB)
                val buffer = ByteArray(1024 * 100) // 100KB буфер
                var totalRead = 0
                var read: Int
                
                while (wrappedDataSource.read(buffer, totalRead, buffer.size - totalRead).also { read = it } != -1) {
                    totalRead += read
                    if (totalRead >= buffer.size) break
                }
                
                val manifestText = String(buffer, 0, totalRead, Charsets.UTF_8)
                android.util.Log.d("KinescopeSDK", "Original HLS manifest size: $totalRead bytes")
                
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
                
                cleanedManifest.toByteArray(Charsets.UTF_8)
            }
            
            manifestBuffer = content
            manifestStream = content?.let { ByteArrayInputStream(it) }
            
            return content?.size?.toLong() ?: bytesRead
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
        return wrappedDataSource.uri
    }
    
    override fun getResponseHeaders(): Map<String, List<String>> {
        return wrappedDataSource.responseHeaders
    }
    
    @Throws(IOException::class)
    override fun close() {
        manifestStream?.close()
        manifestBuffer = null
        isM3u8 = false
        wrappedDataSource.close()
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

