package com.gamo.travelfund.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.TripEntity
import java.time.format.DateTimeFormatter

@Composable
fun TripCard(
    trip: TripEntity,
    onClick: () -> Unit = {},
    savedAmount: Double
) {
    val progress = if (trip.totalBudget > 0)
        (savedAmount / trip.totalBudget).toFloat().coerceIn(0f, 1f)
    else 0f

    val progressPercent = (progress * 100).toInt()

    val today = System.currentTimeMillis()

    val departure = trip.departureDateMillis
    val returnDate = trip.returnDateMillis

    val tripStatus = remember(
        departure,
        returnDate,
        today
    ) {
        when {
            today < departure -> {
                val days =
                    (departure - today) / (1000 * 60 * 60 * 24)

                if (days <= 1)
                    TripStatus.Tomorrow
                else
                    TripStatus.Upcoming(days)
            }

            today in departure..returnDate ->
                TripStatus.InProgress

            else ->
                TripStatus.Finished
        }
    }

    val displayFormatter = DateTimeFormatter.ofPattern("d MMM yyyy", java.util.Locale("es", "MX"))

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,       // surface en lugar de surfaceVariant
            contentColor = MaterialTheme.colorScheme.onSurface      // garantiza que todo el texto encima sea legible
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp, MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // — Header —
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface    // explícito para no heredar accidentalmente
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = trip.destination,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusBadge(tripStatus)
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // — Fechas —
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DateChip(
                    emoji = "✈️",
                    label = "Salida",
                    value = formatDate(trip.departureDateMillis),
                    modifier = Modifier.weight(1f)
                )
                DateChip(
                    emoji = "🏠",
                    label = "Regreso",
                    value = formatDate(trip.returnDateMillis),
                    modifier = Modifier.weight(1f)
                )
            }

            // — Progreso —
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "💰 Ahorrado",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$progressPercent%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = progressColor(progress)
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = progressColor(progress),
                    trackColor = MaterialTheme.colorScheme.surfaceVariant  // superficie, no texto
                )
                Text(
                    text = "$${formatAmount(savedAmount)} / $${formatAmount(trip.totalBudget)} ${trip.baseCurrency}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // — Footer —
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CountdownChip(tripStatus)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "💱 ${trip.destinationCurrency}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DateChip(emoji: String, label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = "$emoji $label",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface    // explícito
        )
    }
}

@Composable
private fun StatusBadge(status: TripStatus) {
    val (label, containerColor, contentColor) = when (status) {
        is TripStatus.Upcoming -> Triple(
            "Planeado",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )

        TripStatus.Tomorrow, TripStatus.Today -> Triple(
            "¡Próximo!",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )

        TripStatus.InProgress -> Triple(
            "En curso",
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer
        )

        TripStatus.Finished -> Triple(
            "Terminado",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )

        TripStatus.Unknown -> Triple(
            "—",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    Surface(shape = RoundedCornerShape(20.dp), color = containerColor) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun CountdownChip(status: TripStatus) {
    val text = when (status) {
        is TripStatus.Upcoming -> "⏳ Faltan ${status.days} días"
        TripStatus.Tomorrow -> "⏳ Viajas mañana"
        TripStatus.Today -> "✈️ Viajas hoy"
        TripStatus.InProgress -> "🌍 Viaje en curso"
        TripStatus.Finished -> "✅ Viaje completado"
        TripStatus.Unknown -> ""
    }
    if (text.isNotEmpty()) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun progressColor(progress: Float) = when {
    progress >= 1f -> MaterialTheme.colorScheme.tertiary
    progress >= 0.7f -> MaterialTheme.colorScheme.primary
    progress >= 0.4f -> MaterialTheme.colorScheme.secondary
    else -> MaterialTheme.colorScheme.error
}

sealed class TripStatus {
    data class Upcoming(val days: Long) : TripStatus()
    data object Tomorrow : TripStatus()
    data object Today : TripStatus()
    data object InProgress : TripStatus()
    data object Finished : TripStatus()
    data object Unknown : TripStatus()
}