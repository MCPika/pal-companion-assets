package com.example.palcompanion.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palcompanion.data.Datasource
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.WorkSuitability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FarmPalViewModel(application: Application) : AndroidViewModel(application) {

    private val _pals = MutableStateFlow<List<Pal>>(emptyList())
    val pals: StateFlow<List<Pal>> = _pals.asStateFlow()

    private val _selectedFarmDrop = MutableStateFlow<String?>(null)
    val selectedFarmDrop: StateFlow<String?> = _selectedFarmDrop.asStateFlow()

    private var allPals: List<Pal> = emptyList()

    val farmDrops = listOf(
        "Arrow",
        "Bone",
        "Cotton Candy",
        "Egg",
        "Flame Organ",
        "Giga Sphere",
        "Gold Coin",
        "High Quality Cloth",
        "High Quality Pal Oil",
        "Honey",
        "Hyper Sphere",
        "Mega Sphere",
        "Milk",
        "Pal Fluids",
        "Pal Sphere",
        "Red Berries",
        "Venom Gland",
        "Wool"
    ).sorted()

    init {
        loadPals()
        viewModelScope.launch {
            selectedFarmDrop.collect { selectedDrop ->
                if (selectedDrop == null) {
                    _pals.value = allPals
                } else {
                    _pals.value = allPals.filter { pal ->
                        pal.drops.any { drop -> drop.name == selectedDrop && drop.special == "Farm Drop" }
                    }
                }
            }
        }
    }

    private fun loadPals() {
        viewModelScope.launch {
            allPals = Datasource(getApplication()).loadPals().filter { pal ->
                pal.workSuitability.any { it.type == WorkSuitability.FARMING }
            }
            _pals.value = allPals
        }
    }

    fun onFarmDropSelected(farmDrop: String) {
        _selectedFarmDrop.value = farmDrop
    }

    fun clearFarmDropSelection() {
        _selectedFarmDrop.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
                FarmPalViewModel(application)
            }
        }
    }
}