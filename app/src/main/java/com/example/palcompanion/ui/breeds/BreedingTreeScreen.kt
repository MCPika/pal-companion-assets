package com.example.palcompanion.ui.breeds

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.data.Breeding
import java.util.UUID
import kotlin.math.max

data class PalNode(
    val palName: String,
    val parents: Pair<PalNode, PalNode>? = null,
    val id: String = UUID.randomUUID().toString()
)

@Composable
fun BreedingTreeRoute(
    modifier: Modifier = Modifier,
) {
    val viewModel: BreedsViewModel = viewModel(factory = BreedsViewModel.Factory)
    val uiState by viewModel.breedsUiState.collectAsState()

    when (val currentState = uiState) {
        is BreedsUiState.Loading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is BreedsUiState.Error -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: Something went wrong.")
            }
        }

        is BreedsUiState.Success -> {
            var rootNode by remember { mutableStateOf(currentState.rootNode) }

            LaunchedEffect(currentState.rootNode) {
                rootNode = currentState.rootNode
            }

            BreedingTreeScreen(
                modifier = modifier,
                combinations = currentState.breeds,
                rootNode = rootNode,
                onBreedingSelected = { viewModel.onBreedingSelected(it) },
                onPalSelected = { palName, nodeId -> viewModel.getBreedsForPal(palName, nodeId) }
            )
        }
    }
}

@Composable
fun BreedingTreeScreen(
    modifier: Modifier = Modifier,
    combinations: List<Breeding>,
    rootNode: PalNode,
    onBreedingSelected: (Breeding) -> Unit,
    onPalSelected: (String, String) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    Column(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(0.7f)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale *= zoom
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY
                    )
            ) {
                BreedingTree(node = rootNode, onPalSelected = onPalSelected, isRoot = true)
            }
        }
        Box(
            modifier = Modifier
                .weight(0.3f)
                .fillMaxSize()
                .background(Color.DarkGray),
        ) {
            if (combinations.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "No breeding combinations found for this Pal.", color = Color.White)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    items(combinations) { combination ->
                        BreedingCard(
                            breeding = combination,
                            onBreedingSelected = onBreedingSelected
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun BreedingTree(node: PalNode, onPalSelected: (String, String) -> Unit, isRoot: Boolean) {
    Layout(
        content = {
            PalNode(
                palName = node.palName,
                isCrowned = isRoot,
                size = if (isRoot) 80.dp else 40.dp,
                onPalSelected = { onPalSelected(node.palName, node.id) }
            )
            node.parents?.let { (parent1, parent2) ->
                BreedingTree(node = parent1, onPalSelected = onPalSelected, isRoot = false)
                BreedingTree(node = parent2, onPalSelected = onPalSelected, isRoot = false)
            }
        }
    ) { measurables, constraints ->
        val palNodePlaceable = measurables.first().measure(constraints)
        val parentPlaceables = if (measurables.size > 1) listOf(measurables[1].measure(constraints), measurables[2].measure(constraints)) else null

        val parent1Width = parentPlaceables?.get(0)?.width ?: 0
        val parent2Width = parentPlaceables?.get(1)?.width ?: 0

        val width = max(palNodePlaceable.width, parent1Width + parent2Width)
        val height = palNodePlaceable.height + (parentPlaceables?.maxOfOrNull { it.height } ?: 0)

        layout(width, height) {
            palNodePlaceable.placeRelative(x = (width - palNodePlaceable.width) / 2, y = 0)
            parentPlaceables?.let {
                it[0].placeRelative(x = 0, y = palNodePlaceable.height)
                it[1].placeRelative(x = parent1Width, y = palNodePlaceable.height)
            }
        }
    }
}


@Composable
fun PalNode(
    palName: String,
    isCrowned: Boolean,
    size: Dp,
    onPalSelected: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(8.dp)
            .clickable { onPalSelected() }
    ) {
        Box(contentAlignment = Alignment.TopCenter) {
            val imageName = palName.replace(' ', '_').lowercase()
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Img/${imageName}.webp")
                    .crossfade(true)
                    .build(),
                contentDescription = palName,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )
            if (isCrowned) {
                AsyncImage(
                    model = "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/crown_icon.png",
                    contentDescription = "Crown",
                    modifier = Modifier
                        .size(30.dp)
                        .offset(y = (-15).dp)
                )
            }
        }
        Text(text = palName, color = Color.White, modifier = Modifier.padding(top = 4.dp))
    }
}
