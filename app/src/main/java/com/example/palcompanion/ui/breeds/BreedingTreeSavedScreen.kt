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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.palcompanion.data.SavedBreedingTree
import com.google.gson.Gson
import java.util.Locale

@Composable
fun BreedingTreeSavedScreen(
    modifier: Modifier = Modifier,
    viewModel: BreedingTreeSavedViewModel = viewModel(factory = BreedingTreeSavedViewModel.Factory)
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
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.toggleSelectionMode() },
                    ) {
                        Text(text = if (state.isSelectionMode) "Cancel" else "Select")
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
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "No saved breeding trees found.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.savedTrees) { savedTree ->
                            SavedBreedingTreeItem(
                                savedTree = savedTree,
                                isSelectionMode = state.isSelectionMode,
                                isSelected = state.selectedIds.contains(savedTree.id),
                                onToggleSelection = { viewModel.toggleTreeSelection(savedTree.id) }
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
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onToggleSelection: () -> Unit
) {
    val palNode = Gson().fromJson(savedTree.treeJson, PalNode::class.java)
    val branchCount = countNodes(palNode) - 1

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.White, MaterialTheme.shapes.medium)
            .clickable(enabled = isSelectionMode) { onToggleSelection() }
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
                    .data("https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Img/${imageName}.webp")
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
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = "Branches: $branchCount")
            }

            Spacer(Modifier.width(16.dp))

            Text(
                text = "#${savedTree.id}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun countNodes(node: PalNode): Int {
    return 1 + (node.parents?.let { countNodes(it.first) + countNodes(it.second) } ?: 0)
}
