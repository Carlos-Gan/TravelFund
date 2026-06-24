package com.gamo.travelfund.ui.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.MovementType
import com.gamo.travelfund.data.model.entity.SavingMovementEntity

@Composable
fun MovementRow(
    movement: SavingMovementEntity,
    onLongClick: () -> Unit = {}
) {
    val isIncome = movement.type == MovementType.INCOME
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isIncome)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = if (isIncome) "💰" else "💸",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column {
                Text(
                    text = movement.note,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = formatDateTime(movement.dateMillis),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "${if (isIncome) "+" else "-"}$${formatAmount(movement.amount)}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isIncome)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error
        )
    }
}

fun formatAmount(value: Double): String =
    if (value % 1.0 == 0.0) "%,.0f".format(value)
    else "%,.2f".format(value)