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
import com.example.palcompanion.ui.breeds.BreedingTreeRoute

sealed class PalCompanionRoute(val route: String) {
    object PalList : PalCompanionRoute("pal_list")
    object PalDetail : PalCompanionRoute("pal_detail/{palName}") {
        fun createRoute(palName: String) = "pal_detail/$palName"
    }

    object Breeds : PalCompanionRoute("breeds?childPalName={childPalName}") {
        fun createRoute(childPalName: String? = null): String {
            return if (childPalName != null) {
                "breeds?childPalName=$childPalName"
            } else {
                "breeds"
            }
        }
    }
    object BreedingTree : PalCompanionRoute("breeding_tree/{palName}") {
        fun createRoute(palName: String) = "breeding_tree/$palName"
    }
}

@Composable
fun PalCompanionNavHost(
    navController: NavHostController,
    viewModel: PalViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = PalCompanionRoute.PalList.route,
        modifier = modifier
    ) {
        composable(PalCompanionRoute.PalList.route) {
            val pals by viewModel.pals.collectAsState()
            PalList(
                palList = pals,
                onPalClicked = { pal ->
                    navController.navigate(PalCompanionRoute.PalDetail.createRoute(pal.name))
                },
                viewModel = viewModel
            )
        }
        composable(
            route = PalCompanionRoute.PalDetail.route,
            arguments = listOf(navArgument("palName") { type = NavType.StringType })
        ) { backStackEntry ->
            val palName = backStackEntry.arguments?.getString("palName")
            val pal = viewModel.getPalByName(palName)
            if (pal != null) {
                PalDetailScreen(pal = pal, navController = navController)
            }
        }
        composable(
            route = PalCompanionRoute.Breeds.route,
            arguments = listOf(navArgument("childPalName") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val childPalName = backStackEntry.arguments?.getString("childPalName")
            BreedsRoute(childPalName = childPalName)
        }
        composable(
             route = PalCompanionRoute.BreedingTree.route,
             arguments = listOf(navArgument("palName") { type = NavType.StringType })
        ) { backStackEntry ->
            BreedingTreeRoute()
        }
    }
}