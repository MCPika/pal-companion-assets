package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.palcompanion.R
import com.example.palcompanion.ui.theme.PalCompanionTheme
import kotlinx.coroutines.launch

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    NavItem("Pal List", Icons.AutoMirrored.Filled.List, PalCompanionRoute.PalList.route),
    NavItem("Farm Pal", Icons.Default.Home, PalCompanionRoute.FarmPal.route)
)

@Composable
fun PalCompanionApp() {
    PalCompanionTheme {
        val navController = rememberNavController()
        val viewModel: PalViewModel = viewModel(factory = PalViewModel.Factory)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(windowInsets = WindowInsets(0, 0, 0, 0)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.palworld_background),
                            contentDescription = "Palworld",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    navItems.forEachIndexed { index, item ->
                        NavigationDrawerItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route)
                                scope.launch {
                                    drawerState.close()
                                }
                            },
                            modifier = if (index == 0) Modifier.padding(top = 16.dp) else Modifier
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    PalAppBar(title = "", onMenuClicked = {
                        scope.launch {
                            drawerState.apply {
                                if (isClosed) open() else close()
                            }
                        }
                    })
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
}
