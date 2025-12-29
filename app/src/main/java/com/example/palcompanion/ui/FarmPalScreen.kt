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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.palcompanion.R
import com.example.palcompanion.model.Drop

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmPalScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: FarmPalViewModel
) {
    val pals by viewModel.pals.collectAsState()
    val selectedFarmDrop by viewModel.selectedFarmDrop.collectAsState()
    val sortedFarmDrops by viewModel.sortedFarmDrops.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    val context = LocalContext.current
    LaunchedEffect(context) {
        viewModel.sortFarmDrops(context)
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedFarmDrop?.let { stringResource(id = it.nameResId) } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text(stringResource(id = R.string.pal_farm_drop)) },
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
                sortedFarmDrops.forEach { farmDrop ->
                    val name = stringResource(id = farmDrop.nameResId)
                    val drop = Drop(name = name)
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = drop.getImageUrl(context)),
                                    contentDescription = name,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(name, modifier = Modifier.padding(start = 8.dp))
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
