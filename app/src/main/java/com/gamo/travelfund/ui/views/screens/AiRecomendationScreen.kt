package com.gamo.travelfund.ui.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.data.remote.GeminiRecommendationResponse
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats
import com.gamo.travelfund.ui.components.AIRecommendationResultCard
import com.gamo.travelfund.ui.components.formatAmount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIRecommendationsScreen(
    trip: TripEntity?,
    categories: List<BudgetCategoryWithStats>,
    recommendation: GeminiRecommendationResponse?,
    loading: Boolean,
    onGenerate: (String) -> Unit,
    onBack: () -> Unit
) {
    var interests by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text("Recomendaciones IA", fontWeight = FontWeight.Medium)
                        Text(
                            trip?.destination ?: "Selecciona un viaje",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar"
                        )
                    }
                }
            )
        }
    ) { padding ->

        if (trip == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Viaje no encontrado")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(trip.name, fontWeight = FontWeight.Medium)
                    Text("Destino: ${trip.destination}")
                    Text("Presupuesto: $${formatAmount(trip.totalBudget)} ${trip.baseCurrency}")
                    Text("Categorías: ${categories.size}")
                }
            }

            OutlinedTextField(
                value = interests,
                onValueChange = { interests = it },
                label = { Text("Gustos o preferencias") },
                placeholder = { Text("Ej. comida local, museos, anime, naturaleza") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    onGenerate(interests)
                },
                enabled = !loading,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(if (loading) "Generando..." else "Generar recomendaciones")
            }

            if (loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            recommendation?.let {
                AIRecommendationResultCard(it)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}