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

    init {
        loadPals()
    }

    private fun loadPals() {
        viewModelScope.launch {
            val allPals = Datasource(getApplication()).loadPals()
            _pals.value = allPals.filter { pal ->
                pal.workSuitability.any { it.type == WorkSuitability.FARMING }
            }
        }
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