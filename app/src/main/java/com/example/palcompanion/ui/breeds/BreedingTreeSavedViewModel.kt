package com.example.palcompanion.ui.breeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palcompanion.PalCompanionApplication
import com.example.palcompanion.data.repository.BreedingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BreedingTreeSavedViewModel(private val breedingRepository: BreedingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<BreedingTreeSavedUiState>(BreedingTreeSavedUiState.Loading)
    val uiState: StateFlow<BreedingTreeSavedUiState> = _uiState.asStateFlow()

    init {
        breedingRepository.getSavedTrees()
            .onEach { trees ->
                _uiState.value = BreedingTreeSavedUiState.Success(trees)
            }
            .launchIn(viewModelScope)
    }

    fun toggleSelectionMode() {
        _uiState.update {
            if (it is BreedingTreeSavedUiState.Success) {
                it.copy(isSelectionMode = !it.isSelectionMode, selectedIds = emptySet())
            } else {
                it
            }
        }
    }

    fun toggleTreeSelection(treeId: Int) {
        _uiState.update {
            if (it is BreedingTreeSavedUiState.Success) {
                val newSelectedIds = it.selectedIds.toMutableSet()
                if (newSelectedIds.contains(treeId)) {
                    newSelectedIds.remove(treeId)
                } else {
                    newSelectedIds.add(treeId)
                }
                it.copy(selectedIds = newSelectedIds)
            } else {
                it
            }
        }
    }

    fun deleteSelectedTrees() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState is BreedingTreeSavedUiState.Success) {
                breedingRepository.deleteTrees(currentState.selectedIds.toList())
                toggleSelectionMode() // Exit selection mode after deletion
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PalCompanionApplication
                val breedingRepository = application.container.breedingRepository
                BreedingTreeSavedViewModel(breedingRepository)
            }
        }
    }
}
