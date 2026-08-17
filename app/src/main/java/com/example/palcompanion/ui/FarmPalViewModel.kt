package com.example.palcompanion.ui

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palcompanion.PalCompanionApplication
import com.example.palcompanion.data.Datasource
import com.example.palcompanion.data.FarmDrop
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.WorkSuitability
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.Locale

class FarmPalViewModel(
    application: Application,
    private val datasource: Datasource
) : AndroidViewModel(application) {

    private val _pals = MutableStateFlow<List<Pal>>(emptyList())
    val pals: StateFlow<List<Pal>> = _pals.asStateFlow()

    private val _selectedFarmDrop = MutableStateFlow<FarmDrop?>(null)
    val selectedFarmDrop: StateFlow<FarmDrop?> = _selectedFarmDrop.asStateFlow()

    private var allPals: List<Pal> = emptyList()
    private var dataJob: Job? = null

    private val _sortedFarmDrops = MutableStateFlow<List<FarmDrop>>(emptyList())
    val sortedFarmDrops: StateFlow<List<FarmDrop>> = _sortedFarmDrops.asStateFlow()

    init {
        viewModelScope.launch {
            selectedFarmDrop.collect { selectedDrop ->
                if (selectedDrop == null) {
                    _pals.value = allPals
                } else {
                    val selectedDropName = selectedDrop.name
                    val currentLanguage = if (AppCompatDelegate.getApplicationLocales().isEmpty) "en" else AppCompatDelegate.getApplicationLocales()[0]?.language ?: "en"
                    _pals.value = allPals.filter { pal ->
                        pal.drops.any { drop ->
                            val match = if (currentLanguage == "fr") {
                                val normalizedDropName = Normalizer.normalize(drop.name, Normalizer.Form.NFD)
                                    .replace("\\p{M}".toRegex(), "")
                                    .lowercase(Locale.ROOT)
                                    .replace("-", " ")
                                    .replace("'", "")
                                    .replace("’", "")
                                val normalizedSelectedDropName = Normalizer.normalize(selectedDropName, Normalizer.Form.NFD)
                                    .replace("\\p{M}".toRegex(), "")
                                    .lowercase(Locale.ROOT)
                                    .replace("-", " ")
                                    .replace("'", "")
                                    .replace("’", "")
                                normalizedDropName == normalizedSelectedDropName
                            } else {
                                drop.name.equals(selectedDropName, ignoreCase = true)
                            }
                            match && drop.special == "Farm Drop"
                        }
                    }
                }
            }
        }
    }

    fun loadData(language: String) {
        dataJob?.cancel()
        dataJob = viewModelScope.launch {
            val drops = datasource.loadFarmDrops(language)
            _sortedFarmDrops.value = drops.sortedBy { it.name }

            allPals = datasource.loadPals(language).filter { pal ->
                pal.workSuitability.any { it.type == WorkSuitability.FARMING }
            }
            _selectedFarmDrop.value = null
            _pals.value = allPals
        }
    }

    fun onFarmDropSelected(farmDrop: FarmDrop) {
        _selectedFarmDrop.value = farmDrop
    }

    fun clearFarmDropSelection() {
        _selectedFarmDrop.value = null
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PalCompanionApplication)
                FarmPalViewModel(application, application.container.datasource)
            }
        }
    }
}
