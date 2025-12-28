package com.example.palcompanion.ui.breeds

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.Constants
import com.example.palcompanion.R
import com.example.palcompanion.data.SavedBreedingTree
import com.example.palcompanion.ui.PalCompanionRoute
import com.google.gson.Gson
import java.util.Locale

@Composable
fun BreedingTreeSavedScreen(
    modifier: Modifier = Modifier,
    viewModel: BreedingTreeSavedViewModel = viewModel(factory = BreedingTreeSavedViewModel.Factory),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        when (val state = uiState) {
            is BreedingTreeSavedUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "Loading...")
                }
            }
            is BreedingTreeSavedUiState.Success -> {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.toggleSelectionMode() },
                    ) {
                        Text(text = if (state.isSelectionMode) "Cancel" else "Select")
                    }
                    if (state.isSelectionMode) {
                        Button(
                            onClick = { viewModel.selectAllTrees() },
                        ) {
                            Text(text = "Select All")
                        }
                    }
                    Button(
                        onClick = { viewModel.deleteSelectedTrees() },
                        enabled = state.isSelectionMode && state.selectedIds.isNotEmpty()
                    ) {
                        Text(text = "Delete")
                    }
                }

                if (state.savedTrees.isEmpty()) {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = R.string.no_saved_breeding_trees_found))
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(state.savedTrees) { index, savedTree ->
                            SavedBreedingTreeItem(
                                savedTree = savedTree,
                                index = index + 1,
                                isSelectionMode = state.isSelectionMode,
                                isSelected = state.selectedIds.contains(savedTree.id),
                                onToggleSelection = { viewModel.toggleTreeSelection(savedTree.id) },
                                onCardClicked = {
                                    navController.navigate(PalCompanionRoute.ViewSavedTree.createRoute(savedTree.treeJson))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SavedBreedingTreeItem(
    savedTree: SavedBreedingTree,
    index: Int,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelection: () -> Unit,
    onCardClicked: () -> Unit
) {
    val palNode = Gson().fromJson(savedTree.treeJson, PalNode::class.java)
    val breedingSteps = countBreedingSteps(palNode)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, MaterialTheme.shapes.medium)
            .clickable {
                if (isSelectionMode) {
                    onToggleSelection()
                } else {
                    onCardClicked()
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() },
                    modifier = Modifier.padding(end = 16.dp)
                )
            }

            val imageName = savedTree.rootPalName.replace(' ', '_').lowercase()
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("${Constants.PALS_IMAGE_URL}/${imageName}.webp")
                    .crossfade(true)
                    .build(),
                contentDescription = savedTree.rootPalName,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.White, CircleShape)
            )

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                val formattedPalName = if (savedTree.rootPalName.contains(" ")) {
                    savedTree.rootPalName.split(" ").joinToString(" ") { word ->
                        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }
                } else {
                    savedTree.rootPalName
                }
                Text(
                    text = formattedPalName,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Breeding Steps: $breedingSteps",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = "#$index",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun countBreedingSteps(node: PalNode): Int {
    val count = if (node.parents != null) 1 else 0
    return count + (node.parents?.let { countBreedingSteps(it.first) + countBreedingSteps(it.second) } ?: 0)
}
