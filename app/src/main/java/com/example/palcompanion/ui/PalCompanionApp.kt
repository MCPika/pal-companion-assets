package com.example.palcompanion.ui

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.palcompanion.Constants
import com.example.palcompanion.R
import com.example.palcompanion.ui.theme.PalCompanionTheme
import kotlinx.coroutines.launch

data class NavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun PalCompanionApp() {
    PalCompanionTheme {
        val navController = rememberNavController()
        val palViewModel: PalViewModel = viewModel(factory = PalViewModel.Factory)
        val farmPalViewModel: FarmPalViewModel = viewModel(factory = FarmPalViewModel.Factory)
        val navBackStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry.value?.destination?.route
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        val appLocales = AppCompatDelegate.getApplicationLocales()
        var currentLanguage by remember { mutableStateOf(if (appLocales.isEmpty) "en" else appLocales[0]?.language ?: "en") }

        LaunchedEffect(currentLanguage) {
            palViewModel.loadPals(currentLanguage)
            farmPalViewModel.loadPals(currentLanguage)
        }

        val navItems = listOf(
            NavItem(
                stringResource(R.string.listing_of_pals),
                Icons.AutoMirrored.Filled.List,
                PalCompanionRoute.PalList.route
            ),
            NavItem(
                stringResource(R.string.farming_pals),
                Icons.Default.Home,
                PalCompanionRoute.FarmPal.route
            ),
            NavItem(
                stringResource(R.string.breeding_tree_saved),
                Icons.Default.Save,
                PalCompanionRoute.BreedingTreeSaved.route
            )
        )

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(windowInsets = WindowInsets(0, 0, 0, 0)) {
                    Column(modifier = Modifier.fillMaxSize()) {
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
                                    stringResource(R.string.listing_of_pals) -> "${Constants.PALS_IMAGE_URL}/lamball.webp"
                                    stringResource(R.string.farming_pals) -> Constants.FARMING_PALS_ICON_URL
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

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.en))
                            Spacer(modifier = Modifier.width(8.dp))
                            Switch(
                                checked = currentLanguage == "fr",
                                onCheckedChange = { isChecked ->
                                    val newLanguage = if (isChecked) "fr" else "en"
                                    if (newLanguage != currentLanguage) {
                                        currentLanguage = newLanguage
                                        LocaleHelper.setLocale(newLanguage)
                                        navController.navigate(PalCompanionRoute.PalList.route)
                                        scope.launch { drawerState.close() }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.fr))
                        }

                        Spacer(modifier = Modifier.weight(0.1f))
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
                    palViewModel = palViewModel,
                    farmPalViewModel = farmPalViewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}
