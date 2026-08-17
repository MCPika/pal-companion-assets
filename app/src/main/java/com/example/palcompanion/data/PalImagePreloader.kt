package com.example.palcompanion.data

import android.content.Context
import coil.imageLoader
import coil.request.ImageRequest
import com.example.palcompanion.Constants
import com.example.palcompanion.model.Drop
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.PalElement
import com.example.palcompanion.model.WorkSuitability
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.roundToInt
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withTimeoutOrNull

class PalImagePreloader(private val context: Context) {
    private data class PreloadTarget(val url: String, val sizeDp: Int)

    suspend fun preload(
        palsByLanguage: Map<String, List<Pal>>,
        farmDropsByLanguage: Map<String, List<FarmDrop>>,
        onProgress: (completed: Int, total: Int) -> Unit
    ) {
        val targets = buildList {
            palsByLanguage.forEach { (language, pals) ->
                pals.forEach { pal ->
                    add(PreloadTarget(pal.imageUrl, PAL_IMAGE_SIZE_DP))
                    pal.drops.forEach { drop ->
                        add(PreloadTarget(drop.getImageUrl(language), ICON_SIZE_DP))
                    }
                }
            }

            farmDropsByLanguage.forEach { (language, farmDrops) ->
                farmDrops.forEach { farmDrop ->
                    add(
                        PreloadTarget(
                            Drop(name = farmDrop.name).getImageUrl(language),
                            ICON_SIZE_DP
                        )
                    )
                }
            }

            PalElement.entries.forEach { element ->
                add(PreloadTarget(element.iconUrl, ICON_SIZE_DP))
                add(PreloadTarget(element.iconIcUrl, ICON_SIZE_DP))
            }
            WorkSuitability.entries.forEach { work ->
                add(PreloadTarget(work.iconUrl, ICON_SIZE_DP))
            }

            add(PreloadTarget(Constants.CANCEL_ICON_URL, ICON_SIZE_DP))
            add(PreloadTarget(Constants.CROWN_ICON_URL, ICON_SIZE_DP))
        }.distinctBy { it.url }

        val total = targets.size
        onProgress(0, total)
        if (total == 0) return

        val density = context.resources.displayMetrics.density
        val completed = AtomicInteger(0)
        val semaphore = Semaphore(MAX_PARALLEL_REQUESTS)

        withTimeoutOrNull(PRELOAD_TIMEOUT_MS) {
            coroutineScope {
                targets.map { target ->
                    async {
                        semaphore.withPermit {
                            val sizePx = (target.sizeDp * density).roundToInt()
                            try {
                                context.imageLoader.execute(
                                    ImageRequest.Builder(context)
                                        .data(target.url)
                                        .size(sizePx, sizePx)
                                        .build()
                                )
                            } catch (cancellation: CancellationException) {
                                throw cancellation
                            } catch (_: Exception) {
                                // A missing image must not block the rest of the warm-up.
                            }
                            onProgress(completed.incrementAndGet(), total)
                        }
                    }
                }.awaitAll()
            }
        }
    }

    private companion object {
        const val MAX_PARALLEL_REQUESTS = 8
        const val PRELOAD_TIMEOUT_MS = 90_000L
        const val PAL_IMAGE_SIZE_DP = 128
        const val ICON_SIZE_DP = 32
    }
}
