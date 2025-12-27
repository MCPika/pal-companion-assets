package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.palcompanion.R
import com.example.palcompanion.model.Filter
import com.example.palcompanion.model.PalElement
import com.example.palcompanion.model.WorkSuitability

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    onDismiss: () -> Unit,
    workSuitabilityFilters: List<Filter>,
    palTypeFilters: List<Filter>,
    jobLevelFilters: List<Filter>,
    selectedWorkSuitabilities: Set<WorkSuitability>,
    selectedPalElements: Set<PalElement>,
    selectedJobLevels: Set<Int>,
    onWorkSuitabilityFilterClicked: (WorkSuitability) -> Unit,
    onPalElementFilterClicked: (PalElement) -> Unit,
    onJobLevelFilterClicked: (Int) -> Unit,
) {
    val workCancelFilter = workSuitabilityFilters.find { it.name == stringResource(R.string.cancel) }
    val otherWorkFilters = workSuitabilityFilters.filter { it.name != stringResource(R.string.cancel) }

    val palTypeCancelFilter = palTypeFilters.find { it.name == stringResource(R.string.cancel) }
    val otherPalTypeFilters = palTypeFilters.filter { it.name != stringResource(R.string.cancel) }

    val jobLevelCancelFilter = jobLevelFilters.find { it.name == stringResource(R.string.cancel) }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 4.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.pal_element),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    otherPalTypeFilters.forEach { filter ->
                        FilterChip(
                            filter = filter,
                            isSelected = filter.palElement in selectedPalElements,
                            onClick = { filter.palElement?.let { onPalElementFilterClicked(it) } }
                        )
                    }
                }
                if (palTypeCancelFilter != null) {
                    FilterChip(filter = palTypeCancelFilter, isSelected = false, onClick = { selectedPalElements.forEach { onPalElementFilterClicked(it) } })
                }
            }

            Text(
                text = stringResource(R.string.pal_work_suitability),
                modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    otherWorkFilters.forEach { filter ->
                        FilterChip(
                            filter = filter,
                            isSelected = filter.workSuitability in selectedWorkSuitabilities,
                            onClick = { filter.workSuitability?.let { onWorkSuitabilityFilterClicked(it) } }
                        )
                    }
                }
                if (workCancelFilter != null) {
                    FilterChip(filter = workCancelFilter, isSelected = false, onClick = { selectedWorkSuitabilities.forEach { onWorkSuitabilityFilterClicked(it) } })
                }
            }

            Text(
                text = stringResource(R.string.pal_work_level),
                modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                FlowRow(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    (1..4).forEach { level ->
                        JobLevelFilterChip(
                            level = level,
                            isSelected = level in selectedJobLevels,
                            onClick = { onJobLevelFilterClicked(level) }
                        )
                    }
                }
                if (jobLevelCancelFilter != null) {
                    FilterChip(filter = jobLevelCancelFilter, isSelected = false, onClick = { selectedJobLevels.forEach { onJobLevelFilterClicked(it) } })
                }
            }
        }
    }
}

@Composable
fun FilterChip(filter: Filter, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .then(
                if (filter.name != stringResource(R.string.cancel)) {
                    Modifier.border(
                        width = 1.dp,
                        color = if (isSelected) Color.Red else Color.White,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(onClick = onClick)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = filter.iconUrl),
            contentDescription = filter.name,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun JobLevelFilterChip(level: Int, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(
                width = 1.dp,
                color = if (isSelected) Color.Red else Color.White,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    ) {
        Text(text = level.toString())
    }
}
