package com.example.palcompanion.ui.breeds

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palcompanion.PalCompanionApplication
import com.example.palcompanion.data.Breeding
import com.example.palcompanion.data.repository.BreedingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface BreedsUiState {
    data class Success(val breeds: List<Breeding>, val rootNode: PalNode, val selectedNodeId: String?) : BreedsUiState
    object Error : BreedsUiState
    object Loading : BreedsUiState
}

class BreedsViewModel(
    private val breedingRepository: BreedingRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _breedsUiState = MutableStateFlow<BreedsUiState>(BreedsUiState.Loading)
    val breedsUiState: StateFlow<BreedsUiState> = _breedsUiState.asStateFlow()

    private val initialPalName: String = savedStateHandle.get<String>("palName") ?: ""

    init {
        if (initialPalName.isNotEmpty()) {
            val rootNode = PalNode(initialPalName)
            _breedsUiState.value = BreedsUiState.Success(emptyList(), rootNode, rootNode.id)
            getBreedsForPal(initialPalName, rootNode.id)
        }
    }

    fun getBreedsForPal(palName: String, nodeId: String) {
        viewModelScope.launch {
            val databaseKey = palName.replace(' ', '_').lowercase()
            val breedingFlow: Flow<List<Breeding>> = breedingRepository.getBreedingCombinationsForChild(databaseKey)

            breedingFlow.collect { breedingList ->
                val validBreedingList = breedingList.filter {
                    it.parent1.isValidPalName() && it.parent2.isValidPalName() && it.child.isValidPalName()
                }
                val sortedList = validBreedingList.sortedWith(compareBy({ it.parent1 }, { it.parent2 }))
                _breedsUiState.update {
                    (it as BreedsUiState.Success).copy(breeds = sortedList, selectedNodeId = nodeId)
                }
            }
        }
    }

    fun onBreedingSelected(breeding: Breeding) {
        val currentState = _breedsUiState.value
        if (currentState is BreedsUiState.Success && currentState.selectedNodeId != null) {
            val newRoot = updateNodeInTree(
                currentState.rootNode,
                currentState.selectedNodeId,
                breeding.parent1.toDisplayName(),
                breeding.parent2.toDisplayName()
            )
            if (newRoot != currentState.rootNode) {
                _breedsUiState.value = currentState.copy(rootNode = newRoot)
            }
        }
    }

    fun clearAll() {
        val currentState = _breedsUiState.value
        if (currentState is BreedsUiState.Success) {
            val newRoot = PalNode(initialPalName)
            _breedsUiState.value = currentState.copy(rootNode = newRoot, selectedNodeId = newRoot.id)
            getBreedsForPal(initialPalName, newRoot.id)
        }
    }

    fun clearNode(nodeId: String) {
        val currentState = _breedsUiState.value
        if (currentState is BreedsUiState.Success) {
            val newRoot = removeSubtree(currentState.rootNode, nodeId)
            _breedsUiState.value = currentState.copy(rootNode = newRoot)
        }
    }

    private fun removeSubtree(currentNode: PalNode, targetNodeId: String): PalNode {
        if (currentNode.id == targetNodeId) {
            return currentNode.copy(parents = null)
        }

        currentNode.parents?.let { (p1, p2) ->
            val updatedP1 = removeSubtree(p1, targetNodeId)
            val updatedP2 = removeSubtree(p2, targetNodeId)
            if (updatedP1 !== p1 || updatedP2 !== p2) {
                return currentNode.copy(parents = Pair(updatedP1, updatedP2))
            }
        }

        return currentNode
    }

    private fun updateNodeInTree(currentNode: PalNode, targetNodeId: String, parent1Name: String, parent2Name: String): PalNode {
        if (currentNode.id == targetNodeId && currentNode.parents == null) {
            return currentNode.copy(parents = Pair(PalNode(parent1Name), PalNode(parent2Name)))
        }

        currentNode.parents?.let { (p1, p2) ->
            val updatedP1 = updateNodeInTree(p1, targetNodeId, parent1Name, parent2Name)
            val updatedP2 = updateNodeInTree(p2, targetNodeId, parent1Name, parent2Name)
            if (updatedP1 !== p1 || updatedP2 !== p2) {
                return currentNode.copy(parents = Pair(updatedP1, updatedP2))
            }
        }

        return currentNode
    }

    private fun String.toDisplayName(): String = this.split('_').joinToString(" ") { it.replaceFirstChar(Char::titlecase) }

    private fun String.isValidPalName(): Boolean {
        return this.isNotBlank() && this.all { it.isLetterOrDigit() || it == '_' }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as PalCompanionApplication)
                val breedingRepository = application.container.breedingRepository
                val savedStateHandle = this.createSavedStateHandle()
                BreedsViewModel(
                    breedingRepository = breedingRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}
