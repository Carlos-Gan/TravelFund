package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.R
import com.gamo.travelfund.data.model.entity.MovementType
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.ui.components.MovementRow
import com.gamo.travelfund.ui.components.formatAmount

@Composable
fun MovementSection(
    movements: List<SavingMovementEntity>,
    baseCurrency: String,
    onMovementLongClick: (SavingMovementEntity) -> Unit
) {
    val reversed = remember(movements) { movements.reversed() }

    val totalIncome = remember(movements) {
        movements.filter { it.type == MovementType.INCOME }.sumOf { it.amount }
    }
    val totalExpense = remember(movements) {
        movements.filter { it.type == MovementType.EXPENSE }.sumOf { it.amount }
    }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // — Header —
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.movimientos),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            if (movements.isNotEmpty()) {
                Text(
                    text = "${movements.size} ${stringResource(R.string.registros)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // — Mini resumen de ingresos vs gastos —
        if (movements.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = stringResource(R.string.entradas),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                        )
                        Text(
                            text = "+$${formatAmount(totalIncome)} $baseCurrency",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Text(
                            text = stringResource(R.string.salidas),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.75f)
                        )
                        Text(
                            text = "-$${formatAmount(totalExpense)} $baseCurrency",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
        }

        // — Lista o estado vacío —
        if (movements.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("💰", style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.sin_movimientos_todav_a),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.toca_para_registrar_tu_primer_ahorro),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            reversed.forEach { movement ->
                MovementRow(
                    movement = movement,
                    onLongClick = { onMovementLongClick(movement) }
                )
            }
        }
    }
}