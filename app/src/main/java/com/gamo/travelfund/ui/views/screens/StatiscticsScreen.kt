package com.gamo.travelfund.ui.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.TripStatus
import com.gamo.travelfund.data.stats.TripWithStats
import com.gamo.travelfund.ui.components.formatAmount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(trips: List<TripWithStats>) {

    val totalTrips    = trips.size
    val totalBudget   = remember(trips) { trips.sumOf { it.trip.totalBudget } }
    val totalSaved    = remember(trips) { trips.sumOf { it.savedAmount } }
    val missingAmount = (totalBudget - totalSaved).coerceAtLeast(0.0)
    val averageProgress = if (totalBudget > 0) (totalSaved / totalBudget * 100).toInt() else 0
    val globalProgress  = if (totalBudget > 0) (totalSaved / totalBudget).toFloat().coerceIn(0f, 1f) else 0f

    val activeTrips   = remember(trips) { trips.count { it.trip.status == TripStatus.ACTIVE || it.trip.status == TripStatus.PLANNED } }
    val finishedTrips = remember(trips) { trips.count { it.trip.status == TripStatus.FINISHED } }

    val bestTrip = remember(trips) {
        trips.maxByOrNull { if (it.trip.totalBudget > 0) it.savedAmount / it.trip.totalBudget else 0.0 }
    }
    val sortedTrips = remember(trips) {
        trips.sortedByDescending { if (it.trip.totalBudget > 0) it.savedAmount / it.trip.totalBudget else 0.0 }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text("Estadísticas", fontWeight = FontWeight.Medium)
                        Text(
                            "Resumen de todos tus viajes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (trips.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("📊", style = MaterialTheme.typography.displayMedium)
                    Text("Sin datos todavía", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
                    Text(
                        "Agrega tu primer viaje para ver estadísticas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // — Progreso global —
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Progreso global",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = "$averageProgress%",
                                    style = MaterialTheme.typography.displaySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Ahorrado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                                )
                                Text(
                                    text = "$${formatAmount(totalSaved)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "de $${formatAmount(totalBudget)} MXN",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { globalProgress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(8.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.15f)
                        )
                    }
                }
            }

            // — Tarjetas de resumen —
            item {
                StatsSectionLabel("Resumen general")
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniStatCard("✈️", "Viajes", totalTrips.toString(), "registrados", Modifier.weight(1f))
                    MiniStatCard("🟢", "Activos", activeTrips.toString(), "en curso", Modifier.weight(1f))
                    MiniStatCard("✅", "Terminados", finishedTrips.toString(), "completados", Modifier.weight(1f))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MiniStatCard(
                        emoji = "💰",
                        title = "Ahorrado",
                        value = "$${formatAmount(totalSaved)}",
                        subtitle = "MXN",
                        modifier = Modifier.weight(1f),
                        valueColor = MaterialTheme.colorScheme.primary
                    )
                    MiniStatCard(
                        emoji = "⏳",
                        title = "Faltante",
                        value = "$${formatAmount(missingAmount)}",
                        subtitle = "MXN",
                        modifier = Modifier.weight(1f),
                        valueColor = MaterialTheme.colorScheme.error
                    )
                }
            }

            // — Ranking de viajes —
            item {
                StatsSectionLabel("Ranking por progreso")
            }

            items(sortedTrips, key = { it.trip.id }) { tripWithStats ->
                val progress = if (tripWithStats.trip.totalBudget > 0)
                    (tripWithStats.savedAmount / tripWithStats.trip.totalBudget).toFloat().coerceIn(0f, 1f)
                else 0f
                val progressPercent = (progress * 100).toInt()

                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tripWithStats.trip.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = tripWithStats.trip.destination,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                text = "$progressPercent%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = when {
                                    progress >= 1f   -> MaterialTheme.colorScheme.tertiary
                                    progress >= 0.7f -> MaterialTheme.colorScheme.primary
                                    progress >= 0.4f -> MaterialTheme.colorScheme.secondary
                                    else             -> MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(8.dp)),
                            color = when {
                                progress >= 1f   -> MaterialTheme.colorScheme.tertiary
                                progress >= 0.7f -> MaterialTheme.colorScheme.primary
                                progress >= 0.4f -> MaterialTheme.colorScheme.secondary
                                else             -> MaterialTheme.colorScheme.error
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Ahorrado: $${formatAmount(tripWithStats.savedAmount)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Meta: $${formatAmount(tripWithStats.trip.totalBudget)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StatsSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(start = 2.dp)
    )
}

@Composable
private fun MiniStatCard(
    emoji: String,
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(emoji, style = MaterialTheme.typography.titleSmall)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = valueColor
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}