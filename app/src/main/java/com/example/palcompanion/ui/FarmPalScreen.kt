package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmPalScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FarmPalViewModel = viewModel(factory = FarmPalViewModel.Factory)
) {
    val pals by viewModel.pals.collectAsState()
    val selectedFarmDrop by viewModel.selectedFarmDrop.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedFarmDrop ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Pal Farming Drop") },
                trailingIcon = {
                    if (selectedFarmDrop != null) {
                        IconButton(onClick = { viewModel.clearFarmDropSelection() }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear selection")
                        }
                    } else {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                },
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp),
                shape = RoundedCornerShape(16.dp),
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                viewModel.farmDrops.forEach { farmDrop ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = "https://cdn.jsdelivr.net/gh/MCPika/pal-companion-assets@main/Pals_Drops/${farmDrop.replace(' ', '_').lowercase()}.png"),
                                    contentDescription = farmDrop,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(farmDrop, modifier = Modifier.padding(start = 8.dp))
                            }
                        },
                        onClick = {
                            viewModel.onFarmDropSelected(farmDrop)
                            expanded = false
                        },
                        modifier = Modifier.height(32.dp)
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            items(pals) { pal ->
                PalListItem(pal = pal, onPalClicked = {
                    navController.navigate(PalCompanionRoute.PalDetail.createRoute(pal.name))
                })
            }
        }
    }
}
