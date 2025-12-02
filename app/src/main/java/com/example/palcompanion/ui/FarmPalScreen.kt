package com.example.palcompanion.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun FarmPalScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FarmPalViewModel = viewModel(factory = FarmPalViewModel.Factory)
) {
    val pals by viewModel.pals.collectAsState()

    LazyColumn(modifier = modifier) {
        items(pals) { pal ->
            PalListItem(pal = pal, onPalClicked = {
                navController.navigate(PalCompanionRoute.PalDetail.createRoute(pal.name))
            })
        }
    }
}
