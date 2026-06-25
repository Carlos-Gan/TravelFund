package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assistant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.R
import com.gamo.travelfund.data.model.entity.*
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailContent(
    trip: TripEntity?,
    movements: List<SavingMovementEntity>,
    categories: List<BudgetCategoryWithStats>,
    exchangeRate: Double,
    onBack: () -> Unit,
    baseCurrency: String,
    onSaveMovement: (SavingMovementEntity) -> Unit,
    onUpdateMovement: (SavingMovementEntity) -> Unit,
    onDeleteMovement: (SavingMovementEntity) -> Unit,
    onSaveCategory: (BudgetCategoryEntity) -> Unit,
    onUpdateCategory: (BudgetCategoryEntity) -> Unit,
    onDeleteCategory: (BudgetCategoryEntity) -> Unit,
    onAiClick: () -> Unit
) {
    var showMovementSheet by remember { mutableStateOf(false) }
    var showCategorySheet by remember { mutableStateOf(false) }

    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("INCOME") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var editingMovement by remember { mutableStateOf<SavingMovementEntity?>(null) }

    var categoryName by remember { mutableStateOf("") }
    var categoryEmoji by remember { mutableStateOf("💰") }
    var categoryAmount by remember { mutableStateOf("") }
    var categoryEssential by remember { mutableStateOf(false) }
    var editingCategory by remember { mutableStateOf<BudgetCategoryEntity?>(null) }

    var selectedMovement by remember { mutableStateOf<SavingMovementEntity?>(null) }
    var showMovementOptions by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf<BudgetCategoryEntity?>(null) }
    var showCategoryOptions by remember { mutableStateOf(false) }

    if (trip == null) {
        Scaffold(
            topBar = {
                MediumTopAppBar(
                    title = { Text(stringResource(R.string.detalle)) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(R.string.viaje_no_encontrado))
            }
        }
        return
    }

    val savedAmount = movements.filter { it.type == MovementType.INCOME }.sumOf { it.amount }
    val spentAmount = movements.filter { it.type == MovementType.EXPENSE }.sumOf { it.amount }
    val realSaved = savedAmount - spentAmount
    val totalBudget = trip.totalBudget
    val missingAmount = totalBudget - realSaved
    val progress = if (totalBudget > 0) (realSaved / totalBudget).toFloat().coerceIn(0f, 1f) else 0f
    val progressPercent = (progress * 100).toInt()

    val convertedSaved = realSaved * exchangeRate
    val convertedMissing = missingAmount * exchangeRate
    val convertedBudget = totalBudget * exchangeRate
    val inverseRate = if (exchangeRate > 0) 1 / exchangeRate else 0.0

    val totalPlanned = categories.sumOf { it.category.plannedAmount }
    val totalSpent = categories.sumOf { it.spentAmount }
    val essentialCount = categories.count { it.category.isEssential }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text(trip.name, fontWeight = FontWeight.Medium)
                        Text(
                            trip.destination,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(onClick = onAiClick) {
                        Icon(Icons.Default.Assistant, null)
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showMovementSheet = true },
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.agregar_movimiento))
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
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

            item {
                TripSummarySection(
                    realSaved = realSaved,
                    missingAmount = missingAmount,
                    totalBudget = totalBudget,
                    convertedSaved = convertedSaved,
                    convertedMissing = convertedMissing,
                    convertedBudget = convertedBudget,
                    baseCurrency = trip.baseCurrency,
                    destinationCurrency = trip.destinationCurrency
                )
            }

            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(
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
                                stringResource(R.string.progreso_del_ahorro),
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

            item {
                TripInfoSection(
                    departureDateMillis = trip.departureDateMillis,
                    returnDateMillis = trip.returnDateMillis,
                    destinationCurrency = trip.destinationCurrency
                )
            }

            item {
                CategorySection(
                    categories = categories,
                    totalSpent = totalSpent,
                    totalPlanned = totalPlanned,
                    essentialCount = essentialCount,
                    baseCurrency = trip.baseCurrency,
                    onAddCategory = { showCategorySheet = true },
                    onCategoryLongClick = { category ->
                        selectedCategory = category
                        showCategoryOptions = true
                    }
                )
            }

            item {
                MovementSection(
                    movements = movements,
                    onMovementLongClick = { movement ->
                        selectedMovement = movement
                        showMovementOptions = true
                    },
                    baseCurrency = baseCurrency
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }

    MovementSheet(
        show = showMovementSheet,
        tripId = trip.id,
        amount = amount,
        onAmountChange = { amount = it },
        note = note,
        onNoteChange = { note = it },
        type = type,
        onTypeChange = { type = it },
        categories = categories,
        selectedCategoryId = selectedCategoryId,
        onSelectedCategoryChange = { selectedCategoryId = it },
        editingMovement = editingMovement,
        onDismiss = { showMovementSheet = false },
        onSaveMovement = onSaveMovement,
        onUpdateMovement = onUpdateMovement,
        onClearForm = {
            editingMovement = null
            amount = ""
            note = ""
            type = "INCOME"
            selectedCategoryId = null
        }
    )

    CategorySheet(
        show = showCategorySheet,
        tripId = trip.id,
        editingCategory = editingCategory,
        categoryName = categoryName,
        onCategoryNameChange = { categoryName = it },
        categoryEmoji = categoryEmoji,
        onCategoryEmojiChange = { categoryEmoji = it },
        categoryAmount = categoryAmount,
        onCategoryAmountChange = { categoryAmount = it },
        categoryEssential = categoryEssential,
        onCategoryEssentialChange = { categoryEssential = it },
        onDismiss = { showCategorySheet = false },
        onSaveCategory = onSaveCategory,
        onUpdateCategory = onUpdateCategory,
        onClearForm = {
            editingCategory = null
            categoryName = ""
            categoryEmoji = "💰"
            categoryAmount = ""
            categoryEssential = false
        }
    )

    MovementOptionsDialog(
        show = showMovementOptions,
        movement = selectedMovement,
        onDismiss = {
            showMovementOptions = false
            selectedMovement = null
        },
        onEdit = { movement ->
            editingMovement = movement
            amount = movement.amount.toString()
            note = movement.note
            type = movement.type.name
            selectedCategoryId = movement.categoryId
            showMovementSheet = true
            showMovementOptions = false
        },
        onDelete = { movement ->
            onDeleteMovement(movement)
            showMovementOptions = false
            selectedMovement = null
        }
    )

    CategoryOptionsDialog(
        show = showCategoryOptions,
        category = selectedCategory,
        onDismiss = {
            showCategoryOptions = false
            selectedCategory = null
        },
        onEdit = { category ->
            editingCategory = category
            categoryName = category.name
            categoryEmoji = category.emoji
            categoryAmount = category.plannedAmount.toString()
            categoryEssential = category.isEssential
            showCategorySheet = true
            showCategoryOptions = false
        },
        onDelete = { category ->
            onDeleteCategory(category)
            showCategoryOptions = false
            selectedCategory = null
        }
    )
}