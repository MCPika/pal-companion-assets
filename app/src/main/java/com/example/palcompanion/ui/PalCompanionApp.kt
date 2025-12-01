package com.example.palcompanion.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.palcompanion.ui.theme.PalCompanionTheme

@Composable
fun PalCompanionApp() {
    PalCompanionTheme {
        val navController = rememberNavController()
        val viewModel: PalViewModel = viewModel(factory = PalViewModel.Factory)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            topBar = {
                if (currentRoute != PalCompanionRoute.BreedingTree.route) {
                    PalAppBar(title = "")
                }
            }
        ) { innerPadding ->
            PalCompanionNavHost(
                navController = navController,
                viewModel = viewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}