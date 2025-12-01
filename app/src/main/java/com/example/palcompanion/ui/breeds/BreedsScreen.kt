package com.example.palcompanion.ui.breeds

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.palcompanion.data.Breeding

@Composable
fun BreedsRoute(
    modifier: Modifier = Modifier,
    childPalName: String?
) {
    val viewModel: BreedsViewModel = viewModel(factory = BreedsViewModel.Factory)
    val breedsUiState by viewModel.breedsUiState.collectAsState()
    BreedsScreen(
        breedsUiState = breedsUiState,
        modifier = modifier,
        onBreedingSelected = { viewModel.onBreedingSelected(it) }
    )
}

@Composable
fun BreedsScreen(
    breedsUiState: BreedsUiState, 
    modifier: Modifier = Modifier,
    onBreedingSelected: (Breeding) -> Unit
) {
    when (breedsUiState) {
        is BreedsUiState.Loading -> {
            // TODO: Replace with a proper loading screen
            Text(text = "Loading...")
        }
        is BreedsUiState.Success -> {
            // The 'breeds' property is now a List, so we can use it directly.
            LazyColumn(modifier = modifier.padding(horizontal = 8.dp)) {
                items(breedsUiState.breeds) { breeding ->
                    BreedingCard(
                        breeding = breeding,
                        modifier = Modifier.padding(vertical = 4.dp),
                        onBreedingSelected = onBreedingSelected
                    )
                }
            }
        }
        is BreedsUiState.Error -> {
            // TODO: Replace with a proper error screen
            Text(text = "Error")
        }
    }
}
