package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.palcompanion.R
import com.example.palcompanion.model.Pal
import com.example.palcompanion.model.PalElement
import com.example.palcompanion.model.WorkSuitability
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PalList(
    palList: List<Pal>,
    onPalClicked: (Pal) -> Unit,
    viewModel: PalViewModel,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedPalElements by viewModel.selectedPalElements.collectAsState()
    val selectedWorkSuitabilities by viewModel.selectedWorkSuitabilities.collectAsState()
    val selectedJobLevels by viewModel.selectedJobLevels.collectAsState()

    var openBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState()

    Column(modifier = modifier) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.searchPals(it) },
            placeholder = { Text(stringResource(R.string.search_lamball_or_001)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 8.dp, end = 8.dp),
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            },
            trailingIcon = {
                IconButton(onClick = { openBottomSheet = true }) {
                    Icon(Icons.Default.FilterAlt, contentDescription = "Filter")
                }
            },
            shape = RoundedCornerShape(16.dp),
        )

        LazyColumn(modifier = Modifier.padding(top = 8.dp)) {
            items(palList) { pal ->
                val formattedPalName = if (pal.name.contains(" ")) {
                    pal.name.split(" ").joinToString(" ") { word ->
                        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }
                } else {
                    pal.name
                }
                PalListItem(pal = pal.copy(name = formattedPalName), onPalClicked = onPalClicked)
            }
        }
    }

    if (openBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { openBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Pal Element")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.clearPalElementFilters() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
                FlowRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    PalElement.values().forEach { element ->
                        val isSelected = selectedPalElements.contains(element)
                        IconButton(onClick = { viewModel.onPalElementFilterClicked(element) }) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.Red else Color.White,
                                        CircleShape
                                    )
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = element.iconIcUrl),
                                    contentDescription = element.name,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Pal Work Suitability")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.clearWorkSuitabilityFilters() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
                FlowRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    WorkSuitability.values().forEach { work ->
                        val isSelected = selectedWorkSuitabilities.contains(work)
                        IconButton(onClick = { viewModel.onWorkSuitabilityFilterClicked(work) }) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.Red else Color.White,
                                        CircleShape
                                    )
                            ) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = work.iconUrl),
                                    contentDescription = stringResource(id = work.displayName),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "Pal Work Level")
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.clearJobLevelFilters() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
                FlowRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    (1..4).forEach { level ->
                        val isSelected = selectedJobLevels.contains(level)
                        IconButton(onClick = { viewModel.onJobLevelFilterClicked(level) }) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .border(
                                        1.dp,
                                        if (isSelected) Color.Red else Color.White,
                                        CircleShape
                                    )
                            ) {
                                Text(text = "$level")
                            }
                        }
                    }
                }
            }
        }
    }
}
