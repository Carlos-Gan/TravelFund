package com.gamo.travelfund.ui.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity
import com.gamo.travelfund.data.model.entity.MovementType
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.ui.components.CategoryCard
import com.gamo.travelfund.ui.components.InfoRow
import com.gamo.travelfund.ui.components.MovementRow
import com.gamo.travelfund.ui.components.SummaryCard
import com.gamo.travelfund.ui.components.formatAmount
import com.gamo.travelfund.ui.components.formatDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailScreen(
    trip: TripEntity?,
    movements: List<SavingMovementEntity>,
    categories: List<BudgetCategoryEntity>,
    exchangeRate: Double,
    onBack: () -> Unit,
    onSaveMovement: (SavingMovementEntity) -> Unit,
    onSaveCategory: (BudgetCategoryEntity) -> Unit,
) {
    var showSheet by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INCOME") }
    var showCategorySheet by remember { mutableStateOf(false) }
    var categoryName by remember { mutableStateOf("") }
    var categoryEmoji by remember { mutableStateOf("💰") }
    var categoryAmount by remember { mutableStateOf("") }
    var categoryEssential by remember { mutableStateOf(false) }

    val savedAmount = movements.filter { it.type == MovementType.INCOME }.sumOf { it.amount }
    val spentAmount = movements.filter { it.type == MovementType.EXPENSE }.sumOf { it.amount }
    val realSaved = savedAmount - spentAmount
    val totalBudget = trip?.totalBudget ?: 0.0
    val missingAmount = totalBudget - realSaved
    val progress = if (totalBudget > 0) (realSaved / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    val progressPercent = (progress * 100).toInt()

    val convertedSaved = realSaved * exchangeRate
    val convertedMissing = missingAmount * exchangeRate
    val convertedBudget = totalBudget * exchangeRate
    val inverseRate = if (exchangeRate > 0) 1 / exchangeRate else 0.0

    // Total planeado en categorías vs presupuesto
    val totalPlanned = categories.sumOf { it.plannedAmount }
    val totalSpent = categories.sumOf { it.spentAmount }
    val essentialCount = categories.count { it.isEssential }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text(trip?.name ?: "Detalle", fontWeight = FontWeight.Medium)
                        if (trip != null) {
                            Text(
                                text = trip.destination,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        },
        floatingActionButton = {
            if (trip != null) {
                FloatingActionButton(
                    onClick = { showSheet = true },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar movimiento")
                }
            }
        }
    ) { padding ->

        if (trip == null) {
            Box(Modifier
                .fillMaxSize()
                .padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🗺️", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(12.dp))
                    Text("Viaje no encontrado", style = MaterialTheme.typography.titleMedium)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // — Tipo de cambio —
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "1 ${trip.baseCurrency} ≈ ${"%.4f".format(exchangeRate)} ${trip.destinationCurrency}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            "1 ${trip.destinationCurrency} ≈ ${"%.2f".format(inverseRate)} ${trip.baseCurrency}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // — Tarjetas de resumen —
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        SummaryCard(
                            label = "Ahorrado",
                            value = "$${formatAmount(realSaved)}",
                            currency = trip.baseCurrency,
                            convertedValue = "$${formatAmount(convertedSaved)} ${trip.destinationCurrency}",
                            color = MaterialTheme.colorScheme.primaryContainer,
                            textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            label = "Faltante",
                            value = "$${formatAmount(missingAmount.coerceAtLeast(0.0))}",
                            currency = trip.baseCurrency,
                            convertedValue = "$${formatAmount(convertedMissing)} ${trip.destinationCurrency}",
                            color = MaterialTheme.colorScheme.errorContainer,
                            textColor = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        SummaryCard(
                            label = "Presupuesto",
                            value = "$${formatAmount(totalBudget)}",
                            currency = trip.baseCurrency,
                            convertedValue = "$${formatAmount(convertedBudget)} ${trip.destinationCurrency}",
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // — Barra de progreso —
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    "Progreso del ahorro",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "$progressPercent%",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }

                // — Info del viaje —
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp,
                            MaterialTheme.colorScheme.outlineVariant
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                "Información del viaje",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            InfoRow("✈️ Salida", formatDateTime(trip.departureDateMillis))
                            InfoRow("🏠 Regreso", formatDateTime(trip.returnDateMillis))
                            InfoRow("💱 Moneda destino", trip.destinationCurrency)
                        }
                    }
                }

                // — Header categorías —
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Categorías",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )
                            if (categories.isNotEmpty()) {
                                Text(
                                    text = "$${formatAmount(totalSpent)} / $${
                                        formatAmount(
                                            totalPlanned
                                        )
                                    } ${trip.baseCurrency}" +
                                            if (essentialCount > 0) " · $essentialCount esenciales" else "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        TextButton(onClick = { showCategorySheet = true }) {
                            Text("+ Agregar")
                        }
                    }
                }

                // — Estado vacío de categorías —
                if (categories.isEmpty()) {
                    item {
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
                                    "Sin categorías todavía",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Organiza tu presupuesto por categorías",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(categories) { category ->
                        CategoryCard(category = category, baseCurrency = trip.baseCurrency)
                    }
                }

                // — Header movimientos —
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Movimientos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        if (movements.isNotEmpty()) {
                            Text(
                                "${movements.size} registros",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                if (movements.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("💰", style = MaterialTheme.typography.displaySmall)
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Sin movimientos todavía",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    "Toca + para registrar tu primer ahorro",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(movements.reversed()) { movement ->
                        MovementRow(movement)
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    // — Bottom sheet movimiento —
    if (showSheet && trip != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false; amount = ""; note = ""; type = "INCOME" },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Agregar movimiento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = type == "INCOME",
                        onClick = { type = "INCOME" },
                        label = { Text("💰 Ahorro") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = type == "EXPENSE",
                        onClick = { type = "EXPENSE" },
                        label = { Text("💸 Gasto") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Monto") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = amount.isNotBlank() && amount.toDoubleOrNull() == null,
                    supportingText = if (amount.isNotBlank() && amount.toDoubleOrNull() == null) ({
                        Text(
                            "Número inválido"
                        )
                    }) else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Nota (opcional)") },
                    placeholder = { Text("Ej. Nómina, ahorro semanal") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        val v = amount.toDoubleOrNull() ?: 0.0
                        if (v > 0) {
                            onSaveMovement(
                                SavingMovementEntity(
                                    tripId = trip.id,
                                    amount = v,
                                    type = MovementType.valueOf(type),
                                    note = note.ifBlank { if (type == "INCOME") "Ahorro" else "Gasto" })
                            )
                            amount = ""; note = ""; type = "INCOME"; showSheet = false
                        }
                    },
                    enabled = (amount.toDoubleOrNull() ?: 0.0) > 0,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text(
                        if (type == "INCOME") "Guardar ahorro" else "Guardar gasto",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    // — Bottom sheet categoría —
    if (showCategorySheet && trip != null) {
        ModalBottomSheet(
            onDismissRequest = { showCategorySheet = false },
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Nueva categoría",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium
                )

                // Sugerencias rápidas de emoji
                val suggestions = listOf(
                    "✈️" to "Vuelos",
                    "🏨" to "Hospedaje",
                    "🍽️" to "Comida",
                    "🎭" to "Entretenimiento",
                    "🚕" to "Transporte",
                    "🛍️" to "Compras",
                    "💊" to "Salud",
                    "📋" to "Otro"
                )

                Text(
                    "Sugerencias",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.take(4).forEach { (emoji, name) ->
                        SuggestionChip(
                            onClick = { categoryEmoji = emoji; categoryName = name },
                            label = {
                                Text(
                                    "$emoji $name",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    suggestions.drop(4).forEach { (emoji, name) ->
                        SuggestionChip(
                            onClick = { categoryEmoji = emoji; categoryName = name },
                            label = {
                                Text(
                                    "$emoji $name",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                HorizontalDivider(
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = categoryEmoji,
                        onValueChange = { categoryEmoji = it.take(2) },
                        label = { Text("Emoji") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(80.dp)
                    )
                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { categoryName = it },
                        label = { Text("Nombre") },
                        placeholder = { Text("Ej. Hospedaje") },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = categoryAmount,
                    onValueChange = { categoryAmount = it },
                    label = { Text("Presupuesto planeado") },
                    prefix = { Text("$") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = categoryAmount.isNotBlank() && categoryAmount.toDoubleOrNull() == null,
                    supportingText = if (categoryAmount.isNotBlank() && categoryAmount.toDoubleOrNull() == null) ({
                        Text(
                            "Número inválido"
                        )
                    }) else null,
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Gasto esencial", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Vuelo, hotel, seguro...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = categoryEssential,
                        onCheckedChange = { categoryEssential = it }
                    )
                }

                Button(
                    onClick = {
                        val v = categoryAmount.toDoubleOrNull() ?: 0.0
                        if (categoryName.isNotBlank() && v > 0) {
                            onSaveCategory(
                                BudgetCategoryEntity(
                                    tripId = trip.id,
                                    name = categoryName.trim(),
                                    emoji = categoryEmoji.ifBlank { "💰" },
                                    plannedAmount = v,
                                    isEssential = categoryEssential,
                                    createdAt = System.currentTimeMillis().toString()
                                )
                            )
                            categoryName = ""; categoryEmoji = "💰"; categoryAmount =
                                ""; categoryEssential = false; showCategorySheet = false
                        }
                    },
                    enabled = categoryName.isNotBlank() && (categoryAmount.toDoubleOrNull()
                        ?: 0.0) > 0,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Guardar categoría", fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(8.dp))
            }
        }
    }
}