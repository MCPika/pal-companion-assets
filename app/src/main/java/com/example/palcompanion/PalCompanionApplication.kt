package com.example.palcompanion

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.palcompanion.data.AppContainer
import com.example.palcompanion.data.DefaultAppContainer

class PalCompanionApplication : Application(), ImageLoaderFactory {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    // This app deliberately warms every Pal portrait before showing the list.
                    // Keep a larger share available so those decoded portraits are not evicted
                    // again immediately on image-dense screens.
                    .maxSizePercent(0.30)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.03)
                    .build()
            }
            .crossfade(false)
            .build()
}
