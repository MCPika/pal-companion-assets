package com.example.palcompanion.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.R
import com.example.palcompanion.data.Datasource
import com.example.palcompanion.model.Pal

@Composable
fun PalListItem(pal: Pal, onPalClicked: (Pal) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 2.dp, vertical = 2.dp)
            .clickable { onPalClicked(pal) },
        border = BorderStroke(1.dp, Color.White)
    ) {
        PalItem(pal = pal)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PalItem(pal: Pal, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "No. ${pal.id}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(8.dp))
                pal.elements.forEach { element ->
                    PalElement(element = element)
                    Spacer(Modifier.width(4.dp))
                }
            }
            Text(
                text = pal.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                pal.workSuitability.forEach { workSuitability ->
                    PalWorkSuitability(
                        workSuitability = workSuitability,
                    )
                }
            }
        }
        if(LocalInspectionMode.current) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = pal.name,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(pal.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = pal.name,
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PalListItemPreview() {
    val previewPals = Datasource(LocalContext.current).loadPals()
    if (previewPals.isNotEmpty()) {
        PalListItem(pal = previewPals.first(), onPalClicked = {})
    }
}
