package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.R
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats
import com.gamo.travelfund.ui.components.CategoryCard
import com.gamo.travelfund.ui.components.formatAmount

@Composable
fun CategorySection(
    categories: List<BudgetCategoryWithStats>,
    totalSpent: Double,
    totalPlanned: Double,
    essentialCount: Int,
    baseCurrency: String,
    onAddCategory: () -> Unit,
    onCategoryLongClick: (BudgetCategoryEntity) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(R.string.categor_as),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                if (categories.isNotEmpty()) {
                    Text(
                        text = "$${formatAmount(totalSpent)} / $${formatAmount(totalPlanned)} $baseCurrency" +
                                if (essentialCount > 0) " · $essentialCount ${stringResource(R.string.esenciales)}" else "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            TextButton(onClick = onAddCategory) {
                Text(stringResource(R.string.agregar))
            }
        }

        if (categories.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("🗂️", style = MaterialTheme.typography.headlineLarge)

                    Text(
                        text = stringResource(R.string.sin_categor_as_todav_a),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = stringResource(R.string.organiza_tu_presupuesto_por_categor_as),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            categories.forEach { categoryStats ->
                CategoryCard(
                    category = categoryStats.category,
                    spentAmount = categoryStats.spentAmount,
                    baseCurrency = baseCurrency,
                    onLongClick = {
                        onCategoryLongClick(categoryStats.category)
                    }
                )
            }
        }
    }
}