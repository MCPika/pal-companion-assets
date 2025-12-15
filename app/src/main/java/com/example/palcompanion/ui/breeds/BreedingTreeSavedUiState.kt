package com.example.palcompanion.ui.breeds

import com.example.palcompanion.data.SavedBreedingTree

sealed interface BreedingTreeSavedUiState {
    object Loading : BreedingTreeSavedUiState
    data class Success(
        val savedTrees: List<SavedBreedingTree>,
        val isSelectionMode: Boolean = false,
        val selectedIds: Set<Int> = emptySet()
    ) : BreedingTreeSavedUiState
}
