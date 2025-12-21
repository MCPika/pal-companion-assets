package com.example.palcompanion.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.palcompanion.PalCompanionApplication
import com.example.palcompanion.data.Datasource
import com.example.palcompanion.data.repository.BreedingRepository
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.PalElement
import com.example.palcompanion.model.WorkSuitability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PalViewModel(
    private val datasource: Datasource,
    private val breedingRepository: BreedingRepository
) : ViewModel() {

    private val _pals = MutableStateFlow<List<Pal>>(emptyList())
    val pals: StateFlow<List<Pal>> = _pals.asStateFlow()

    private val _breedingCombos = MutableStateFlow<Map<String, List<com.example.palcompanion.data.Breeding>>>(emptyMap())
    val breedingCombos: StateFlow<Map<String, List<com.example.palcompanion.data.Breeding>>> = _breedingCombos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var allPals: List<Pal> = emptyList()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedWorkSuitabilities = MutableStateFlow<Set<WorkSuitability>>(emptySet())
    val selectedWorkSuitabilities: StateFlow<Set<WorkSuitability>> = _selectedWorkSuitabilities.asStateFlow()

    private val _selectedPalElements = MutableStateFlow<Set<PalElement>>(emptySet())
    val selectedPalElements: StateFlow<Set<PalElement>> = _selectedPalElements.asStateFlow()

    private val _selectedJobLevels = MutableStateFlow<Set<Int>>(emptySet())
    val selectedJobLevels: StateFlow<Set<Int>> = _selectedJobLevels.asStateFlow()

    init {
        loadPals()
        loadBreedingCombos()
        viewModelScope.launch {
            combine(
                _searchQuery,
                _selectedWorkSuitabilities,
                _selectedPalElements,
                _selectedJobLevels
            ) { query, workFilters, elementFilters, jobLevelFilters ->
                val filteredPals = allPals.filter { pal ->
                    val matchesQuery = if (query.isEmpty()) {
                        true
                    } else {
                        val isNumeric = query.all { it.isDigit() }
                        if (isNumeric) {
                            pal.id.contains(query)
                        } else {
                            pal.name.contains(query, ignoreCase = true)
                        }
                    }

                    val palWorkSuitabilities = pal.workSuitability
                    val palWorkTypes = palWorkSuitabilities.map { it.type }.toSet()

                    val matchesWorkFilters = workFilters.isEmpty() || palWorkTypes.containsAll(workFilters)

                    val matchesJobLevelFilters = if (jobLevelFilters.isEmpty()) {
                        true
                    } else {
                        if (workFilters.isEmpty()) {
                            palWorkSuitabilities.any { it.level in jobLevelFilters }
                        } else {
                            palWorkSuitabilities
                                .filter { it.type in workFilters }
                                .any { it.level in jobLevelFilters }
                        }
                    }

                    val matchesElementFilters = elementFilters.isEmpty() || pal.elements.containsAll(elementFilters)

                    matchesQuery && matchesWorkFilters && matchesJobLevelFilters && matchesElementFilters
                }
                _pals.value = filteredPals
            }.collect { }
        }
    }

    private fun loadPals() {
        viewModelScope.launch {
            allPals = datasource.loadPals()
            _pals.value = allPals
            _isLoading.value = false
        }
    }

    private fun loadBreedingCombos() {
        viewModelScope.launch {
            val breedingCombinations = breedingRepository.getBreedingCombinations().first()
            val combosMap = breedingCombinations.groupBy { it.child }.mapValues { (_, value) ->
                value.map { com.example.palcompanion.data.Breeding(it.parent1, it.parent2, it.child) }
            }
            _breedingCombos.value = combosMap
        }
    }

    fun searchPals(query: String) {
        _searchQuery.value = query
    }

    fun onWorkSuitabilityFilterClicked(workSuitability: WorkSuitability) {
        val currentFilters = _selectedWorkSuitabilities.value.toMutableSet()
        if (workSuitability in currentFilters) {
            currentFilters.remove(workSuitability)
        } else {
            currentFilters.add(workSuitability)
        }
        _selectedWorkSuitabilities.value = currentFilters
    }

    fun onPalElementFilterClicked(palElement: PalElement) {
        val currentFilters = _selectedPalElements.value.toMutableSet()
        if (palElement in currentFilters) {
            currentFilters.remove(palElement)
        } else {
            currentFilters.add(palElement)
        }
        _selectedPalElements.value = currentFilters
    }

    fun onJobLevelFilterClicked(level: Int) {
        val currentFilters = _selectedJobLevels.value.toMutableSet()
        if (level in currentFilters) {
            currentFilters.remove(level)
        } else {
            currentFilters.add(level)
        }
        _selectedJobLevels.value = currentFilters
    }

    fun clearPalElementFilters() {
        _selectedPalElements.value = emptySet()
    }

    fun clearWorkSuitabilityFilters() {
        _selectedWorkSuitabilities.value = emptySet()
    }

    fun clearJobLevelFilters() {
        _selectedJobLevels.value = emptySet()
    }

    fun getPalByName(name: String?): Pal? {
        if (name.isNullOrEmpty()) {
            return null
        }
        return allPals.find { it.name.equals(name, ignoreCase = true) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as PalCompanionApplication)
                PalViewModel(application.container.datasource, application.container.breedingRepository)
            }
        }
    }
}
