package com.example.palcompanion.ui.breeds

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.R
import com.example.palcompanion.data.Breeding

@Composable
fun BreedingCard(
    breeding: Breeding, 
    modifier: Modifier = Modifier,
    onBreedingSelected: (Breeding) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onBreedingSelected(breeding) },
        border = BorderStroke(1.dp, Color.White)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PalIcon(name = breeding.parent1, modifier = Modifier.weight(2f))
            Box(
                modifier = Modifier.weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "+", fontWeight = FontWeight.Bold)
            }
            PalIcon(name = breeding.parent2, modifier = Modifier.weight(2f))
            Box(
                modifier = Modifier.weight(0.5f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "=", fontWeight = FontWeight.Bold)
            }
            PalIcon(name = breeding.child, modifier = Modifier.weight(2f))
        }
    }
}

@Composable
fun PalIcon(name: String, modifier: Modifier = Modifier) {
    // Transforms "green_slime" into "Green Slime" for display.
    val displayName = name.split('_').joinToString(" ") { it.replaceFirstChar(Char::titlecase) }
    
    // This guard prevents crashes if the name is blank or contains invalid characters.
    val isNameValid = name.isNotBlank() && name.all { it.isLetterOrDigit() || it == '_' }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (LocalInspectionMode.current || !isNameValid) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = if (isNameValid) displayName else "Invalid Pal",
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        } else {
            // Use the raw database name (e.g., "green_slime") directly in the URL, as you correctly pointed out.
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Img/${name}.webp")
                    .crossfade(true)
                    .build(),
                contentDescription = displayName,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
        }
        Box(modifier = Modifier.height(32.dp), contentAlignment = Alignment.Center) {
            Text(
                text = if (isNameValid) displayName else "Unknown",
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                lineHeight = 13.sp,
                maxLines = 2,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BreedingCardPreview() {
    val sampleBreeding = Breeding(
        parent1 = "enchanted_sword",
        parent2 = "blue_slime",
        child = "blue_slime"
    )
    BreedingCard(breeding = sampleBreeding) {}
}
