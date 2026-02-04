package org.strawberryfoundations.reply.core

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import java.io.File

object AvatarCache {
    private const val DISK_CACHE_SIZE = 50L * 1024 * 1024 // 50 MB
    private const val MEMORY_CACHE_SIZE = 0.25
    private const val CACHE_DIRECTORY = "avatar_cache"

    fun getImageLoader(context: Context): ImageLoader {
        val cacheDirectory = File(context.cacheDir, CACHE_DIRECTORY)

        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(MEMORY_CACHE_SIZE)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDirectory)
                    .maxSizeBytes(DISK_CACHE_SIZE)
                    .build()
            }
            .crossfade(true)
            .respectCacheHeaders(false)
            .build()
    }

    fun clearCache(context: Context) {
        val cacheDirectory = File(context.cacheDir, CACHE_DIRECTORY)
        if (cacheDirectory.exists()) {
            cacheDirectory.deleteRecursively()
        }
    }

    fun getCacheSize(context: Context): Long {
        val cacheDirectory = File(context.cacheDir, CACHE_DIRECTORY)
        return if (cacheDirectory.exists()) {
            cacheDirectory.walkTopDown()
                .filter { it.isFile }
                .map { it.length() }
                .sum()
        } else {
            0L
        }
    }

    fun formatCacheSize(bytes: Long): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "%.2f KB".format(bytes / 1024.0)
            else -> "%.2f MB".format(bytes / (1024.0 * 1024.0))
        }
    }
}
