package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.ui.components.SummaryCard
import com.gamo.travelfund.ui.components.formatAmount

@Composable
fun TripSummarySection(
    realSaved: Double,
    missingAmount: Double,
    totalBudget: Double,
    convertedSaved: Double,
    convertedMissing: Double,
    convertedBudget: Double,
    baseCurrency: String,
    destinationCurrency: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SummaryCard(
            label = "Ahorrado",
            value = "$${formatAmount(realSaved)}",
            currency = baseCurrency,
            convertedValue = "$${formatAmount(convertedSaved)} $destinationCurrency",
            color = MaterialTheme.colorScheme.primaryContainer,
            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            label = "Faltante",
            value = "$${formatAmount(missingAmount.coerceAtLeast(0.0))}",
            currency = baseCurrency,
            convertedValue = "$${formatAmount(convertedMissing)} $destinationCurrency",
            color = MaterialTheme.colorScheme.errorContainer,
            textColor = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.weight(1f)
        )

        SummaryCard(
            label = "Presupuesto",
            value = "$${formatAmount(totalBudget)}",
            currency = baseCurrency,
            convertedValue = "$${formatAmount(convertedBudget)} $destinationCurrency",
            color = MaterialTheme.colorScheme.surfaceVariant,
            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}