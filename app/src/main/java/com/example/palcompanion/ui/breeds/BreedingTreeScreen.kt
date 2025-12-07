package com.example.palcompanion.ui.breeds

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.data.Breeding
import java.util.Locale
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
    viewModel: BreedsViewModel = viewModel(factory = BreedsViewModel.Factory)
) {
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
                onPalSelected = { palName, nodeId -> viewModel.getBreedsForPal(palName, nodeId) },
                onClearOne = { viewModel.clearNode(currentState.selectedNodeId ?: "") },
                onClearAll = { viewModel.clearAll() },
                isClearOneEnabled = currentState.selectedNodeId != null && currentState.selectedNodeId != currentState.rootNode.id,
                selectedNodeId = currentState.selectedNodeId
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
    onPalSelected: (String, String) -> Unit,
    onClearOne: () -> Unit,
    onClearAll: () -> Unit,
    isClearOneEnabled: Boolean,
    selectedNodeId: String?
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
                modifier = Modifier.graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
            ) {
                BreedingTree(node = rootNode, onPalSelected = onPalSelected, isRoot = true, selectedNodeId = selectedNodeId)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onClearOne, enabled = isClearOneEnabled) {
                Text(text = "Clear One")
            }
            Button(onClick = onClearAll) {
                Text(text = "Clear All")
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
fun BreedingTree(node: PalNode, onPalSelected: (String, String) -> Unit, isRoot: Boolean, selectedNodeId: String?) {
    val horizontalSpacing = 16.dp
    val verticalSpacing = 40.dp

    SubcomposeLayout { constraints ->
        val palNodePlaceable = subcompose("palNode") {
            PalNode(
                palName = node.palName,
                isCrowned = isRoot,
                size = if (isRoot) 80.dp else 40.dp,
                onPalSelected = { onPalSelected(node.palName, node.id) },
                isSelected = node.id == selectedNodeId
            )
        }.first().measure(constraints)

        val parentPlaceables = subcompose("parents") {
            node.parents?.let { (p1, p2) ->
                BreedingTree(node = p1, onPalSelected = onPalSelected, isRoot = false, selectedNodeId = selectedNodeId)
                BreedingTree(node = p2, onPalSelected = onPalSelected, isRoot = false, selectedNodeId = selectedNodeId)
            }
        }.map { it.measure(constraints) }

        if (parentPlaceables.isEmpty()) {
            layout(palNodePlaceable.width, palNodePlaceable.height) {
                palNodePlaceable.placeRelative(0, 0)
            }
        } else {
            val p1 = parentPlaceables[0]
            val p2 = parentPlaceables[1]

            val p1Width = p1.width
            val p2Width = p2.width
            val horizontalSpacingPx = horizontalSpacing.toPx()

            val parentsWidth = p1Width + horizontalSpacingPx + p2Width

            val childX = (parentsWidth - palNodePlaceable.width) / 2f

            val width = max(parentsWidth, palNodePlaceable.width.toFloat()).toInt()
            val height = (palNodePlaceable.height + verticalSpacing.toPx() + max(p1.height, p2.height)).toInt()

            val canvasPlaceable = subcompose("canvas") {
                Canvas(modifier = Modifier.size(width.toDp(), height.toDp())) {
                    val childBottomY = palNodePlaceable.height.toFloat()
                    val parentsTopY = palNodePlaceable.height + verticalSpacing.toPx()
                    val middleY = (childBottomY + parentsTopY) / 2f

                    val childCenterX = childX + palNodePlaceable.width / 2f
                    val parent1CenterX = p1Width / 2f
                    val parent2CenterX = p1Width + horizontalSpacingPx + p2Width / 2f

                    // Vertical Line from child to T-junction
                    drawLine(
                        color = Color.White,
                        start = Offset(childCenterX, childBottomY),
                        end = Offset(childCenterX, middleY),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Horizontal T-junction line
                    drawLine(
                        color = Color.White,
                        start = Offset(parent1CenterX, middleY),
                        end = Offset(parent2CenterX, middleY),
                        strokeWidth = 2.dp.toPx()
                    )

                    // Lines down to parents
                    drawLine(
                        color = Color.White,
                        start = Offset(parent1CenterX, middleY),
                        end = Offset(parent1CenterX, parentsTopY),
                        strokeWidth = 2.dp.toPx()
                    )
                    drawLine(
                        color = Color.White,
                        start = Offset(parent2CenterX, middleY),
                        end = Offset(parent2CenterX, parentsTopY),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }.first().measure(Constraints.fixed(width, height))

            layout(width, height) {
                canvasPlaceable.placeRelative(0, 0)
                palNodePlaceable.placeRelative(childX.toInt(), 0)
                p1.placeRelative(0, palNodePlaceable.height + verticalSpacing.toPx().toInt())
                p2.placeRelative(p1Width + horizontalSpacing.toPx().toInt(), palNodePlaceable.height + verticalSpacing.toPx().toInt())
            }
        }
    }
}

@Composable
fun PalNode(
    palName: String,
    isCrowned: Boolean,
    size: Dp,
    onPalSelected: () -> Unit,
    isSelected: Boolean
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
                    .border(
                        1.dp,
                        if (isSelected) Color.Red else Color.White,
                        CircleShape
                    )
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
        val formattedPalName = if (palName.contains(" ")) {
            palName.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            }
        } else {
            palName
        }
        Text(text = formattedPalName, color = Color.White, modifier = Modifier.padding(top = 4.dp))
    }
}
