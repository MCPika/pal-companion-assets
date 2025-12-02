package com.example.palcompanion.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.palcompanion.R
import com.example.palcompanion.model.Pal
import java.util.Locale

@Composable
fun PalDetailScreen(pal: Pal, navController: NavController) {
    val titleColor = Color(0xFF00ACC1)
    val powerCooldownColor = Color(0xFFFFB52E)
    val levelColor = Color(0xFF43A6C6)
    val specialDropColor = Color(0xFFFFB52E)

    val formattedPalName = if (pal.name.contains(" ")) {
        pal.name.split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }
    } else {
        pal.name
    }

    Box(modifier = Modifier.padding(start = 8.dp, end = 8.dp)) {
        LazyColumn {
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = rememberAsyncImagePainter(model = pal.imageUrl),
                        contentDescription = formattedPalName,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .size(128.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text(
                            text = "#${pal.id} $formattedPalName",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        pal.elements.forEach { element ->
                            Image(
                                painter = rememberAsyncImagePainter(model = element.iconIcUrl),
                                contentDescription = element.name,
                                modifier = Modifier
                                    .size(32.dp)
                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Button(
                    onClick = { navController.navigate(PalCompanionRoute.BreedingTree.createRoute(pal.name)) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(R.string.go_to_breeding_tree))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.description),
                        style = MaterialTheme.typography.headlineSmall,
                        color = titleColor
                    )
                    Text(text = pal.description, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        buildAnnotatedString {
                            withStyle(style = MaterialTheme.typography.headlineSmall.toSpanStyle().copy(color = titleColor)) {
                                append(stringResource(R.string.partner_skill))
                            }
                            withStyle(style = MaterialTheme.typography.titleMedium.toSpanStyle().copy(color = Color(0xFFFFA500))) {
                                append(" ")
                                append(pal.partnerSkill.name)
                            }
                        }
                    )
                    Text(
                        text = pal.partnerSkill.description,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.work_skills),
                        style = MaterialTheme.typography.headlineSmall,
                        color = titleColor
                    )
                    pal.workSuitability.forEach { work ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(model = work.type.iconUrl),
                                contentDescription = stringResource(work.type.displayName),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(text = " ${stringResource(work.type.displayName)} - ${stringResource(R.string.level)} ${work.level}")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.drops),
                        style = MaterialTheme.typography.headlineSmall,
                        color = titleColor
                    )
                    pal.drops.forEach { drop ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Image(
                                painter = rememberAsyncImagePainter(model = drop.imageUrl),
                                contentDescription = drop.name,
                                modifier = Modifier.size(24.dp)
                            )
                            if (!drop.special.isNullOrEmpty()) {
                                Text(text = " ${drop.name} - ")
                                Text(text = drop.special, color = specialDropColor)
                            } else {
                                Text(text = " ${drop.name} (${drop.quantity ?: ""}) - ${drop.rate ?: ""}")
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.active_skills),
                        style = MaterialTheme.typography.headlineSmall,
                        color = titleColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    pal.activeSkills.forEachIndexed { index, skill ->
                        Column(modifier = Modifier.padding(bottom = 8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${stringResource(R.string.level)} ${skill.level} - ",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = levelColor
                                )
                                Image(
                                    painter = rememberAsyncImagePainter(model = skill.element.iconIcUrl),
                                    contentDescription = skill.element.name,
                                    modifier = Modifier.size(24.dp)
                                )
                                Text(
                                    text = " ${skill.name}",
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(text = skill.description, style = MaterialTheme.typography.bodyMedium)
                            Row {
                                Text(text = "${stringResource(R.string.cooldown)}: ${skill.cooldown}", color = powerCooldownColor)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "${stringResource(R.string.power)}: ${skill.power}", color = powerCooldownColor)
                            }
                        }
                        if (index < pal.activeSkills.lastIndex) {
                            HorizontalDivider(color = Color.White, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
    }
}
