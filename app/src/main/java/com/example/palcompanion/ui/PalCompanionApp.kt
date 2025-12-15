package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.palcompanion.R
import com.example.palcompanion.ui.theme.PalCompanionTheme
import kotlinx.coroutines.launch

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val navItems = listOf(
    NavItem("Listing of Pals", Icons.AutoMirrored.Filled.List, PalCompanionRoute.PalList.route),
    NavItem("Farming Pals", Icons.Default.Home, PalCompanionRoute.FarmPal.route),
    NavItem("Breeding Tree Saved", Icons.Default.Save, PalCompanionRoute.BreedingTreeSaved.route)
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

                    // Custom items to bypass the NavigationDrawerItem bug
                    navItems.forEach { item ->
                        val isSelected = currentRoute == item.route
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .clip(RoundedCornerShape(100))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                )
                                .clickable {
                                    navController.navigate(item.route)
                                    scope.launch { drawerState.close() }
                                }
                                .padding(start = 16.dp, end = 24.dp, top = 12.dp, bottom = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val imageUrl = when (item.label) {
                                "Listing of Pals" -> "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Img/lamball.webp"
                                "Farming Pals" -> "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Jobs/farming.webp"
                                else -> null
                            }

                            if (imageUrl != null) {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Icon(item.icon, contentDescription = item.label)
                            }

                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = item.label,
                                color = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    val topRoutes = navItems.map { it.route }
                    PalAppBar(
                        title = "",
                        onMenuClicked = {
                            scope.launch {
                                drawerState.apply {
                                    if (isClosed) open() else close()
                                }
                            }
                        },
                        onBackClicked = if (currentRoute != null && !topRoutes.contains(currentRoute)) {
                            { navController.popBackStack() }
                        } else {
                            null
                        }
                    )
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
