package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.palcompanion.model.PalElement

@Composable
fun PalElement(element: PalElement, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(element.iconUrl),
        contentDescription = element.name,
        contentScale = ContentScale.FillHeight,
        modifier = Modifier.height(18.dp).then(modifier)
    )
}
