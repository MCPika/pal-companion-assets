package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.palcompanion.model.PalWorkSuitability

@Composable
fun PalWorkSuitability(workSuitability: PalWorkSuitability, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(workSuitability.type.iconUrl),
            contentDescription = workSuitability.type.name,
            modifier = Modifier.size(24.dp)
        )
        //Spacer(modifier = Modifier.width(2.dp))
        Text(
            text = workSuitability.level.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
