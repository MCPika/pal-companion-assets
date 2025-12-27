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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.R
import com.example.palcompanion.model.ActiveSkill
import com.example.palcompanion.model.Drop
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.PalElement
import com.example.palcompanion.model.PalWorkSuitability
import com.example.palcompanion.model.PartnerSkill
import com.example.palcompanion.model.WorkSuitability

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
                    text = stringResource(R.string.pal_number_prefix) + pal.id,
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
    val previewPal = Pal(
        id = "001",
        name = "Lamball",
        elements = listOf(PalElement.NORMAL),
        workSuitability = listOf(
            PalWorkSuitability(WorkSuitability.HANDIWORK, 1),
            PalWorkSuitability(WorkSuitability.TRANSPORTING, 1),
            PalWorkSuitability(WorkSuitability.FARMING, 1)
        ),
        imageUrl = "",
        description = "A fluffy, spherical pal that is famously docile. If it gets attacked, it will flee with all its might, but may fall and roll away.",
        partnerSkill = PartnerSkill(name = "Fluffy Shield", description = "When activated, equips a fluffy shield to the player."),
        drops = listOf(
            Drop(name = "Wool", quantity = "1-2", rate = "100%"),
            Drop(name = "Lamball Mutton", quantity = "1", rate = "100%")
        ),
        activeSkills = listOf(
            ActiveSkill(level = 1, name = "Roly Poly", description = "Curses a targeted enemy with negative status effects.", cooldown = 1, power = 10, element = PalElement.NORMAL)
        )
    )
    PalListItem(pal = previewPal, onPalClicked = {})
}
