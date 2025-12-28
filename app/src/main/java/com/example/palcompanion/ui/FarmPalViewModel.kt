package com.example.palcompanion.ui

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palcompanion.PalCompanionApplication
import com.example.palcompanion.R
import com.example.palcompanion.data.Datasource
import com.example.palcompanion.data.FarmDrop
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.WorkSuitability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FarmPalViewModel(
    application: Application,
    private val datasource: Datasource
) : AndroidViewModel(application) {

    private val _pals = MutableStateFlow<List<Pal>>(emptyList())
    val pals: StateFlow<List<Pal>> = _pals.asStateFlow()

    private val _selectedFarmDrop = MutableStateFlow<FarmDrop?>(null)
    val selectedFarmDrop: StateFlow<FarmDrop?> = _selectedFarmDrop.asStateFlow()

    private var allPals: List<Pal> = emptyList()

    private val _sortedFarmDrops = MutableStateFlow<List<FarmDrop>>(emptyList())
    val sortedFarmDrops: StateFlow<List<FarmDrop>> = _sortedFarmDrops.asStateFlow()

    val farmDrops = listOf(
        FarmDrop("Arrow", R.string.farm_drop_arrow),
        FarmDrop("Bone", R.string.farm_drop_bone),
        FarmDrop("Cotton Candy", R.string.farm_drop_cotton_candy),
        FarmDrop("Egg", R.string.farm_drop_egg),
        FarmDrop("Flame Organ", R.string.farm_drop_flame_organ),
        FarmDrop("Giga Sphere", R.string.farm_drop_giga_sphere),
        FarmDrop("Gold Coin", R.string.farm_drop_gold_coin),
        FarmDrop("High Quality Cloth", R.string.farm_drop_high_quality_cloth),
        FarmDrop("High Quality Pal Oil", R.string.farm_drop_high_quality_pal_oil),
        FarmDrop("Honey", R.string.farm_drop_honey),
        FarmDrop("Hyper Sphere", R.string.farm_drop_hyper_sphere),
        FarmDrop("Mega Sphere", R.string.farm_drop_mega_sphere),
        FarmDrop("Milk", R.string.farm_drop_milk),
        FarmDrop("Pal Fluids", R.string.farm_drop_pal_fluids),
        FarmDrop("Pal Sphere", R.string.farm_drop_pal_sphere),
        FarmDrop("Red Berries", R.string.farm_drop_red_berries),
        FarmDrop("Venom Gland", R.string.farm_drop_venom_gland),
        FarmDrop("Wool", R.string.farm_drop_wool)
    )

    init {
        sortFarmDrops(getApplication<Application>().applicationContext)
        val appLocales = AppCompatDelegate.getApplicationLocales()
        val language = if (appLocales.isEmpty) "en" else appLocales[0]?.language ?: "en"
        loadPals(language)
        viewModelScope.launch {
            selectedFarmDrop.collect { selectedDrop ->
                if (selectedDrop == null) {
                    _pals.value = allPals
                } else {
                    val selectedDropName = getApplication<Application>().getString(selectedDrop.nameResId)
                    _pals.value = allPals.filter { pal ->
                        pal.drops.any { drop ->
                            drop.name.equals(selectedDropName, ignoreCase = true) && drop.special == "Farm Drop"
                        }
                    }
                }
            }
        }
    }

    fun sortFarmDrops(context: Context) {
        _sortedFarmDrops.value = farmDrops.sortedBy { context.getString(it.nameResId) }
    }

    fun loadPals(language: String) {
        viewModelScope.launch {
            allPals = datasource.loadPals(language).filter { pal ->
                pal.workSuitability.any { it.type == WorkSuitability.FARMING }
            }
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