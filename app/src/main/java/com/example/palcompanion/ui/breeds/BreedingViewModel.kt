package com.example.palcompanion.ui.breeds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palcompanion.PalCompanionApplication
import com.example.palcompanion.data.repository.BreedingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class BreedingViewModel(breedingRepository: BreedingRepository) : ViewModel() {
    val breedingCombinations = breedingRepository.getBreedingCombinations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PalCompanionApplication
                val breedingRepository = application.container.breedingRepository
                BreedingViewModel(breedingRepository)
            }
        }
    }
}