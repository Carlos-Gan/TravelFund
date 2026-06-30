package com.gamo.travelfund.ui.views.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.R
import com.gamo.travelfund.data.model.entity.TripStatus
import com.gamo.travelfund.data.stats.TripWithStats
import com.gamo.travelfund.ui.components.formatAmount
import com.gamo.travelfund.ui.components.statistics.EmptyStatisticsState
import com.gamo.travelfund.ui.components.statistics.StatisticsSectionHeader
import com.gamo.travelfund.ui.components.statistics.TripRankingCard
import com.gamo.travelfund.ui.components.statistics.progress

/**
 * Progreso de ahorro del viaje.
 *
 * Puede ser superior a 1.0 cuando el usuario ahorró más
 * que el presupuesto establecido.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    trips: List<TripWithStats>
) {
    val totalTrips = trips.size

    val totalBudget = remember(trips) {
        trips.sumOf { it.trip.totalBudget }
    }

    val totalSaved = remember(trips) {
        trips.sumOf { it.savedAmount }
    }

    val missingAmount = remember(totalBudget, totalSaved) {
        (totalBudget - totalSaved).coerceAtLeast(0.0)
    }

    val averageProgress = remember(totalBudget, totalSaved) {
        if (totalBudget > 0) {
            ((totalSaved / totalBudget) * 100).toInt()
        } else {
            0
        }
    }

    val globalProgress = remember(totalBudget, totalSaved) {
        if (totalBudget > 0) {
            (totalSaved / totalBudget)
                .toFloat()
                .coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    val activeTrips = remember(trips) {
        trips.count {
            it.trip.status == TripStatus.ACTIVE ||
                    it.trip.status == TripStatus.PLANNED
        }
    }

    val finishedTrips = remember(trips) {
        trips.count {
            it.trip.status == TripStatus.FINISHED
        }
    }

    val sortedTrips = remember(trips) {
        trips.sortedByDescending { it.progress }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.estadisticas),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = stringResource(
                                R.string.resumen_de_todos_tus_viajes
                            ),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = Color.Unspecified,
                    navigationIconContentColor = Color.Unspecified,
                    titleContentColor = Color.Unspecified,
                    actionIconContentColor = Color.Unspecified
                )
            )
        }
    ) { paddingValues ->

        if (trips.isEmpty()) {
            EmptyStatisticsState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )

            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 12.dp,
                end = 16.dp,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            item {
                GlobalProgressCard(
                    averageProgress = averageProgress,
                    globalProgress = globalProgress,
                    totalSaved = totalSaved,
                    totalBudget = totalBudget
                )
            }

            item {
                StatisticsSectionHeader(
                    title = stringResource(R.string.resumen_general)
                )
            }

            item {
                StatisticsGrid(
                    totalTrips = totalTrips,
                    activeTrips = activeTrips,
                    finishedTrips = finishedTrips,
                    missingAmount = missingAmount
                )
            }

            item {
                Spacer(modifier = Modifier.height(2.dp))
            }

            item {
                StatisticsSectionHeader(
                    title = stringResource(R.string.ranking_por_progreso),
                    amount = sortedTrips.size
                )
            }

            itemsIndexed(
                items = sortedTrips,
                key = { _, item -> item.trip.id }
            ) { index, tripWithStats ->

                TripRankingCard(
                    position = index + 1,
                    tripWithStats = tripWithStats
                )
            }
        }
    }
}

@Composable
private fun GlobalProgressCard(
    averageProgress: Int,
    globalProgress: Float,
    totalSaved: Double,
    totalBudget: Double
) {
    val animatedProgress by animateFloatAsState(
        targetValue = globalProgress,
        animationSpec = tween(durationMillis = 700),
        label = "globalProgress"
    )

    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.tertiaryContainer
                    )
                )
            )
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(96.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 9.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.45f
                    )
                )

                Text(
                    text = "$averageProgress%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.progreso_global),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.75f
                    )
                )

                Text(
                    text = "$${formatAmount(totalSaved)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = stringResource(R.string.ahorrado),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.8f
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${stringResource(R.string.meta)}: " +
                            "$${formatAmount(totalBudget)} MXN",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(
                        alpha = 0.7f
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun StatisticsGrid(
    totalTrips: Int,
    activeTrips: Int,
    finishedTrips: Int,
    missingAmount: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernStatCard(
                icon = Icons.Default.Place,
                title = stringResource(R.string.viajes),
                value = totalTrips.toString(),
                subtitle = stringResource(R.string.registrados),
                accentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )

            ModernStatCard(
                icon = Icons.Default.DateRange,
                title = stringResource(R.string.activos),
                value = activeTrips.toString(),
                subtitle = stringResource(R.string.en_curso),
                accentColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModernStatCard(
                icon = Icons.Default.Check,
                title = stringResource(R.string.terminados),
                value = finishedTrips.toString(),
                subtitle = stringResource(R.string.completados),
                accentColor = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.weight(1f)
            )

            ModernStatCard(
                icon = Icons.Default.Info,
                title = stringResource(R.string.faltante),
                value = "$${formatAmount(missingAmount)}",
                subtitle = "MXN",
                accentColor = MaterialTheme.colorScheme.error,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ModernStatCard(
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.heightIn(min = 138.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                modifier = Modifier.size(42.dp),
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.12f)
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}