package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.gamo.travelfund.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.MovementType
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.stats.BudgetCategoryWithStats

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovementSheet(
    show: Boolean,
    tripId: Long,
    amount: String,
    onAmountChange: (String) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    type: String,
    onTypeChange: (String) -> Unit,
    categories: List<BudgetCategoryWithStats>,
    selectedCategoryId: Long?,
    onSelectedCategoryChange: (Long?) -> Unit,
    editingMovement: SavingMovementEntity?,
    onDismiss: () -> Unit,
    onSaveMovement: (SavingMovementEntity) -> Unit,
    onUpdateMovement: (SavingMovementEntity) -> Unit,
    onClearForm: () -> Unit
) {
    if (!show) return

    val defaultIncomeNote = stringResource(R.string.ahorro)
    val defaultExpenseNote = stringResource(R.string.gasto)

    ModalBottomSheet(
        onDismissRequest = {
            onClearForm()
            onDismiss()
        },
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
                text = if (editingMovement == null) stringResource(R.string.agregar_movimiento) else stringResource(
                    R.string.editar_movimiento
                ),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = type == "INCOME",
                    onClick = {
                        onTypeChange("INCOME")
                        onSelectedCategoryChange(null)
                    },
                    label = { Text("💰 ${stringResource(R.string.ahorro)}") },
                    modifier = Modifier.weight(1f)
                )

                FilterChip(
                    selected = type == "EXPENSE",
                    onClick = { onTypeChange("EXPENSE") },
                    label = { Text("💸 ${stringResource(R.string.gasto)}") },
                    modifier = Modifier.weight(1f)
                )
            }

            if (type == "EXPENSE" && categories.isNotEmpty()) {
                Text(
                    text = "Categoría",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { categoryStats ->
                        val category = categoryStats.category

                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = { onSelectedCategoryChange(category.id) },
                            label = {
                                Text("${category.emoji} ${category.name}")
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                label = { Text(stringResource(R.string.monto)) },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amount.isNotBlank() && amount.toDoubleOrNull() == null,
                supportingText = if (amount.isNotBlank() && amount.toDoubleOrNull() == null) {
                    { Text(stringResource(R.string.n_mero_inv_lido)) }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = note,
                onValueChange = onNoteChange,
                label = { Text(stringResource(R.string.nota_opcional)) },
                placeholder = { Text(stringResource(R.string.ej_n_mina_ahorro_semanal)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            val isValidAmount = (amount.toDoubleOrNull() ?: 0.0) > 0
            val isExpenseWithoutCategory = type == "EXPENSE" && selectedCategoryId == null

            Button(
                onClick = {
                    val value = amount.toDoubleOrNull() ?: 0.0

                    val movementToSave = editingMovement?.copy(
                        amount = value,
                        categoryId = if (type == "EXPENSE") selectedCategoryId else null,
                        type = MovementType.valueOf(type),
                        note = note.ifBlank {
                            if (type == "INCOME") defaultIncomeNote else defaultExpenseNote
                        }
                    ) ?: SavingMovementEntity(
                        tripId = tripId,
                        categoryId = if (type == "EXPENSE") selectedCategoryId else null,
                        amount = value,
                        type = MovementType.valueOf(type),
                        note = note.ifBlank {
                            if (type == "INCOME") defaultIncomeNote else defaultExpenseNote
                        }
                    )

                    if (editingMovement != null) {
                        onUpdateMovement(movementToSave)
                    } else {
                        onSaveMovement(movementToSave)
                    }

                    onClearForm()
                    onDismiss()
                },
                enabled = isValidAmount && !isExpenseWithoutCategory,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (editingMovement == null) {
                        if (type == "INCOME") stringResource(R.string.guardar_ahorro) else stringResource(
                            R.string.guardar_gasto
                        )
                    } else {
                        stringResource(R.string.guardar_cambios)
                    },
                    fontWeight = FontWeight.Medium
                )
            }

            if (isExpenseWithoutCategory) {
                Text(
                    text = stringResource(R.string.selecciona_una_categor_a_para_registrar_el_gasto),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}