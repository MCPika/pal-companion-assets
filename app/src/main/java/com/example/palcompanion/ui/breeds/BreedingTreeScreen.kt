package com.example.palcompanion.ui.breeds

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.data.Breeding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID
import kotlin.math.max

data class PalNode(
    val palName: String,
    val parents: Pair<PalNode, PalNode>? = null,
    val id: String = UUID.randomUUID().toString()
)

// AnimPair used for animation state per node
data class AnimPair(val x: Animatable<Float, AnimationVector1D>, val y: Animatable<Float, AnimationVector1D>)

// MNode mirrors the measured tree; defined top-level so drawLinks can reference it
data class MNode(
    val node: PalNode,
    val placeIndex: Int,
    val width: Int,
    val height: Int,
    val children: List<MNode>,
    var x: Float = 0f,
    var y: Float = 0f
)

// Top-level draw function — must not be @Composable. Called from inside Canvas { }.
fun DrawScope.drawLinks(
    m: MNode,
    anims: Map<String, AnimPair>,
    verticalSpacingPx: Float
) {
    fun rec(node: MNode) {
        val animParent = anims[node.node.id]!!
        val pX = animParent.x.value
        val pY = animParent.y.value
        val parentCenterX = pX + node.width / 2f
        val parentBottomY = pY + node.height

        for (child in node.children) {
            val animChild = anims[child.node.id]!!
            val cX = animChild.x.value
            val cY = animChild.y.value
            val childCenterX = cX + child.width / 2f

            // --- Draw connecting lines (unchanged except using safe vertical spacing) ---
            val safeChildTop = kotlin.math.max(cY, parentBottomY + verticalSpacingPx)
            val midY = (parentBottomY + safeChildTop) / 2f

            drawLine(Color.White, Offset(parentCenterX, parentBottomY), Offset(parentCenterX, midY), 3.dp.toPx())
            drawLine(Color.White, Offset(parentCenterX, midY), Offset(childCenterX, midY), 3.dp.toPx())
            drawLine(Color.White, Offset(childCenterX, midY), Offset(childCenterX, safeChildTop), 3.dp.toPx())

            // -------------------------------------------------------------------------
            // ARROWHEAD → pointing UP into *PARENT ONLY*
            // -------------------------------------------------------------------------
            val arrowWidth = 10.dp.toPx()
            val arrowHeight = 12.dp.toPx()

            val arrowTipY = parentBottomY                   // tip touches parent bottom
            val arrowBaseY = parentBottomY + arrowHeight    // base extends downward

            val arrowPath = Path().apply {
                moveTo(parentCenterX, arrowTipY)                // tip into parent
                lineTo(parentCenterX - arrowWidth, arrowBaseY)  // bottom-left
                lineTo(parentCenterX + arrowWidth, arrowBaseY)  // bottom-right
                close()
            }

            drawPath(arrowPath, Color.White)

            rec(child)
        }
    }

    rec(m)
}


@Composable
fun BreedingTreeRoute(
    modifier: Modifier = Modifier,
    viewModel: BreedsViewModel = viewModel(factory = BreedsViewModel.Factory)
) {
    val uiState by viewModel.breedsUiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.eventChannel) {
        viewModel.eventChannel.collectLatest { event ->
            when (event) {
                is BreedsViewEvent.TreeSaved -> {
                    snackbarHostState.showSnackbar("Pal breeding tree has been saved.")
                }
            }
        }
    }

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
                onSaveTree = { viewModel.saveTree() },
                isClearOneEnabled = currentState.selectedNodeId != null && currentState.selectedNodeId != currentState.rootNode.id,
                isSaveTreeEnabled = currentState.rootNode.parents != null,
                selectedNodeId = currentState.selectedNodeId,
                snackbarHostState = snackbarHostState
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreedingTreeScreen(
    modifier: Modifier = Modifier,
    combinations: List<Breeding>,
    rootNode: PalNode,
    onBreedingSelected: (Breeding) -> Unit,
    onPalSelected: (String, String) -> Unit,
    onClearOne: () -> Unit,
    onClearAll: () -> Unit,
    onSaveTree: () -> Unit,
    isClearOneEnabled: Boolean,
    isSaveTreeEnabled: Boolean,
    selectedNodeId: String?,
    snackbarHostState: SnackbarHostState
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val scaffoldState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        BottomSheetScaffold(
            modifier = modifier,
            scaffoldState = scaffoldState,
            sheetPeekHeight = 0.dp,
            sheetContent = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.9f)
                        .background(Color.DarkGray),
                ) {
                    if (combinations.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Select a Pal to see breeding combinations.",
                                color = Color.White
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(vertical = 2.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            items(combinations) { combination ->
                                BreedingCard(
                                    breeding = combination,
                                    onBreedingSelected = {
                                        // call original callback
                                        onBreedingSelected(it)
                                        // collapse the bottom sheet automatically
                                        scope.launch {
                                            scaffoldState.bottomSheetState.partialExpand() // collapse to peek height
                                            // OR: use scaffoldState.bottomSheetState.hide() for full hide
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = max(0.5f, scale * zoom)
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
                    BreedingTree(
                        node = rootNode,
                        onPalSelected = { palName, nodeId ->
                            onPalSelected(palName, nodeId)
                            scope.launch {
                                scaffoldState.bottomSheetState.expand()
                            }
                        },
                        isRoot = true,
                        selectedNodeId = selectedNodeId
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(innerPadding)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = {
                            onClearOne()
                            scope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                        },
                        enabled = isClearOneEnabled
                    ) {
                        Text(text = "Clear One")
                    }

                    Button(
                        onClick = {
                            onSaveTree()
                            scope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                        },
                        enabled = isSaveTreeEnabled
                    ) {
                        Text(text = "Save Tree")
                    }

                    Button(
                        onClick = {
                            onClearAll()
                            scope.launch {
                                scaffoldState.bottomSheetState.partialExpand()
                            }
                        }
                    ) {
                        Text(text = "Clear All")
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .wrapContentWidth(),
            snackbar = { snackbarData ->
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .border(1.dp, Color.White, MaterialTheme.shapes.medium),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(
                        text = snackbarData.visuals.message,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }
}


@Composable
fun BreedingTree(
    node: PalNode,
    onPalSelected: (String, String) -> Unit,
    isRoot: Boolean,
    selectedNodeId: String?,
    horizontalSpacing: Dp = 24.dp,
    verticalSpacing: Dp = 48.dp
) {
    val density = LocalDensity.current
    val hSpacingPx = with(density) { horizontalSpacing.toPx() }
    val vSpacingPx = with(density) { verticalSpacing.toPx() }

    val anims = remember { mutableStateMapOf<String, AnimPair>() }
    val coroutineScope = rememberCoroutineScope()

    SubcomposeLayout { constraints ->

        // 1) Measure - build measured tree
        val placeables = mutableListOf<Placeable>()

        fun measureNode(p: PalNode, depth: Int = 0): MNode {
            val tag = "node-${p.id}"
            val measuredPlaceable = subcompose(tag) {
                PalNode(
                    palName = p.palName,
                    isCrowned = depth == 0 && isRoot,
                    size = if (depth == 0 && isRoot) 80.dp else 40.dp,
                    onPalSelected = { onPalSelected(p.palName, p.id) },
                    isSelected = p.id == selectedNodeId
                )
            }.first().measure(constraints)

            val index = placeables.size
            placeables += measuredPlaceable

            val children = if (p.parents != null) {
                listOf(measureNode(p.parents.first, depth + 1), measureNode(p.parents.second, depth + 1))
            } else emptyList()

            // initialize anims entry if missing (values will be animated to targets later)
            if (!anims.containsKey(p.id)) {
                anims[p.id] = AnimPair(Animatable(0f), Animatable(0f))
            }

            return MNode(
                node = p,
                placeIndex = index,
                width = measuredPlaceable.width,
                height = measuredPlaceable.height,
                children = children
            )
        }

        val rootMeasured = measureNode(node)

        // 2) Assign positions: children positioned relative to parent bottom + vSpacingPx
        var cursor = 0f

        fun assignPositions(m: MNode, parentBottom: Float? = null) {
            m.y = if (parentBottom == null) 0f else parentBottom + vSpacingPx

            if (m.children.isEmpty()) {
                m.x = cursor
                cursor += m.width + hSpacingPx
            } else {
                val bottom = m.y + m.height
                m.children.forEach { assignPositions(it, bottom) }

                val left = m.children.first()
                val right = m.children.last()
                val center = (left.x + (right.x + right.width)) / 2f
                m.x = center - m.width / 2f
            }
        }

        assignPositions(rootMeasured, null)

        // 3) Animate to targets (spring)
        fun animateTree(m: MNode) {
            val pair = anims[m.node.id]!!
            coroutineScope.launch {
                pair.x.animateTo(
                    targetValue = m.x,
                    animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            }
            coroutineScope.launch {
                pair.y.animateTo(
                    targetValue = m.y,
                    animationSpec = spring(stiffness = Spring.StiffnessLow, dampingRatio = Spring.DampingRatioMediumBouncy)
                )
            }
            m.children.forEach { animateTree(it) }
        }
        animateTree(rootMeasured)

        // 4) Compute layout bounds
        fun computeBounds(m: MNode): Pair<Int, Int> {
            var maxX = (m.x + m.width).toInt()
            var maxY = (m.y + m.height).toInt()
            m.children.forEach {
                val (cx, cy) = computeBounds(it)
                if (cx > maxX) maxX = cx
                if (cy > maxY) maxY = cy
            }
            return maxX to maxY
        }

        val (measuredW, measuredH) = computeBounds(rootMeasured)

        val layoutW = measuredW.coerceAtLeast(constraints.minWidth)
        val layoutH = measuredH.coerceAtLeast(constraints.minHeight)

        // 5) Layout: draw canvas + place nodes
        layout(layoutW, layoutH) {
            // draw connections in a canvas subcompose (reads animated positions)
            val canvasPlaceable = subcompose("canvas") {
                Canvas(modifier = Modifier.size(with(density) { layoutW.toDp() }, with(density) { layoutH.toDp() })) {
                    // call top-level drawLinks (DrawScope receiver)
                    drawLinks(rootMeasured, anims, vSpacingPx)
                }
            }.first().measure(Constraints.fixed(layoutW, layoutH))

            canvasPlaceable.place(0, 0)

            // place nodes at current animated positions
            fun placeRec(m: MNode) {
                val a = anims[m.node.id]!!
                placeables[m.placeIndex].place(a.x.value.toInt(), a.y.value.toInt())
                m.children.forEach { placeRec(it) }
            }
            placeRec(rootMeasured)
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
