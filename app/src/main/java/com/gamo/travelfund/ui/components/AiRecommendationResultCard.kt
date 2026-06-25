package com.gamo.travelfund.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.remote.GeminiRecommendationResponse

@Composable
fun AIRecommendationResultCard(
    recommendation: GeminiRecommendationResponse
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (recommendation.summary.isNotBlank()) {
            SectionCard("✨ Resumen") {
                Text(recommendation.summary)
            }
        }

        if (recommendation.recommendedPlaces.isNotEmpty()) {
            SectionCard("📍 Lugares recomendados") {
                recommendation.recommendedPlaces.forEach { place ->
                    Text(place.name, fontWeight = FontWeight.Medium)
                    Text(place.reason, style = MaterialTheme.typography.bodySmall)
                    Text(
                        "Costo: ${place.estimatedCost}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        if (recommendation.budgetTips.isNotEmpty()) {
            SectionCard("💰 Consejos para ahorrar") {
                recommendation.budgetTips.forEach { tip ->
                    Text("• $tip", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        if (recommendation.suggestedCategories.isNotEmpty()) {
            SectionCard("📋 Categorías sugeridas") {
                recommendation.suggestedCategories.forEach { category ->
                    Text(
                        "${category.emoji} ${category.name}",
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        category.reason,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        if (recommendation.budgetAnalysis.isNotEmpty()) {
            SectionCard("📊 Análisis de presupuesto") {
                recommendation.budgetAnalysis.forEach { analysis ->
                    Text(analysis.category, fontWeight = FontWeight.Medium)
                    Text(
                        analysis.comment,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            0.5.dp,
            MaterialTheme.colorScheme.outlineVariant
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(
                thickness = 0.5.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )

            content()
        }
    }
}