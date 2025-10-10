package io.kinescope.sdk.player

import android.net.Uri
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.hls.playlist.HlsPlaylistParser
import androidx.media3.exoplayer.hls.playlist.HlsPlaylist
import java.io.InputStream

/**
 * Custom HLS playlist parser который удаляет DRM теги (#EXT-X-KEY)
 * Это позволяет воспроизводить HLS потоки с DRM тегами как незашифрованный контент
 */
@UnstableApi
class NoDrmHlsPlaylistParser : HlsPlaylistParser() {
    
    override fun parse(uri: Uri, inputStream: InputStream): HlsPlaylist {
        // Читаем весь манифест как строку
        val manifestContent = inputStream.bufferedReader().use { it.readText() }
        
        // Удаляем все #EXT-X-KEY строки (DRM теги)
        val cleanedContent = manifestContent.lines()
            .filterNot { it.trim().startsWith("#EXT-X-KEY:") }
            .joinToString("\n")
        
        android.util.Log.d("KinescopeSDK", "Removed DRM tags from HLS manifest")
        
        // Парсим очищенный манифест через родительский parser
        return super.parse(uri, cleanedContent.byteInputStream())
    }
}

