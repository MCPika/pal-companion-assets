package com.example.palcompanion.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

/**
 * Shared remote image renderer.
 *
 * AsyncImage resolves the requested bitmap size from its Compose constraints, so Coil does not
 * decode a full-size asset for a small icon. The placeholder also keeps the UI stable while the
 * first network request completes; subsequent requests are served by the app-wide caches.
 */
@Composable
fun RemoteImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center
) {
    AsyncImage(
        model = model,
        contentDescription = contentDescription,
        placeholder = ColorPainter(Color.Transparent),
        error = ColorPainter(Color.Transparent),
        contentScale = contentScale,
        alignment = alignment,
        modifier = modifier
    )
}
