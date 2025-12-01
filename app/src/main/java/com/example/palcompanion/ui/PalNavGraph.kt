package com.example.palcompanion.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.palcompanion.ui.breeds.BreedsRoute

sealed class Screen(val route: String) {
    object PalList : Screen("pal_list")
    object PalDetail : Screen("pal_detail/{palId}") {
        fun createRoute(palId: String) = "pal_detail/$palId"
    }
    object Breeds : Screen("breeds?childPalName={childPalName}") {
        fun createRoute(childPalName: String?) = "breeds?childPalName=$childPalName"
    }
}

@Composable
fun PalNavGraph(navController: NavHostController, viewModel: PalViewModel, modifier: Modifier = Modifier) {
    val pals by viewModel.pals.collectAsState()

    NavHost(
        navController = navController, 
        startDestination = Screen.PalList.route,
        modifier = modifier
    ) {
        composable(Screen.PalList.route) {
            PalList(
                palList = pals,
                onPalClicked = { navController.navigate(Screen.PalDetail.createRoute(it.id)) },
                viewModel = viewModel
            )
        }
        composable(Screen.PalDetail.route) { backStackEntry ->
            val palId = backStackEntry.arguments?.getString("palId")
            palId?.let {
                val pal = pals.find { pal -> pal.id == it }
                pal?.let { pal ->
                    PalDetailScreen(pal = pal, navController = navController)
                }
            }
        }
        composable(
            route = Screen.Breeds.route,
            arguments = listOf(navArgument("childPalName") { 
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val childPalName = backStackEntry.arguments?.getString("childPalName")
            BreedsRoute(childPalName = childPalName)
        }
    }
}
