package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity

private val categorySuggestions = listOf(
    "✈️" to "Vuelos",
    "🏨" to "Hospedaje",
    "🍽️" to "Comida",
    "🎭" to "Entrete.",
    "🚕" to "Transporte",
    "🛍️" to "Compras",
    "💊" to "Salud",
    "📋" to "Otro"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySheet(
    show: Boolean,
    tripId: Long,
    editingCategory: BudgetCategoryEntity?,
    categoryName: String,
    onCategoryNameChange: (String) -> Unit,
    categoryEmoji: String,
    onCategoryEmojiChange: (String) -> Unit,
    categoryAmount: String,
    onCategoryAmountChange: (String) -> Unit,
    categoryEssential: Boolean,
    onCategoryEssentialChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSaveCategory: (BudgetCategoryEntity) -> Unit,
    onUpdateCategory: (BudgetCategoryEntity) -> Unit,
    onClearForm: () -> Unit
) {
    if (!show) return

    val isEditing = editingCategory != null
    val amountError = categoryAmount.isNotBlank() && categoryAmount.toDoubleOrNull() == null
    val formValid = categoryName.isNotBlank() && (categoryAmount.toDoubleOrNull() ?: 0.0) > 0

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // — Título —
            Text(
                text = if (isEditing) "Editar categoría" else "Nueva categoría",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium
            )

            // — Chips de sugerencias en un FlowRow —
            Text(
                text = "Sugerencias",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(categorySuggestions) { (emoji, name) ->
                    val isSelected = categoryEmoji == emoji && categoryName == name
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onCategoryEmojiChange(emoji)
                            onCategoryNameChange(name)
                        },
                        label = {
                            Text(
                                "$emoji $name",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)

            // — Emoji + Nombre —
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = categoryEmoji,
                    onValueChange = { onCategoryEmojiChange(it.take(2)) },
                    label = { Text("Emoji") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(80.dp)
                )
                OutlinedTextField(
                    value = categoryName,
                    onValueChange = onCategoryNameChange,
                    label = { Text("Nombre") },
                    placeholder = { Text("Ej. Hospedaje") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                )
            }

            // — Presupuesto —
            OutlinedTextField(
                value = categoryAmount,
                onValueChange = onCategoryAmountChange,
                label = { Text("Presupuesto planeado") },
                prefix = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError,
                supportingText = if (amountError) ({ Text("Número inválido") }) else null,
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // — Switch esencial —
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Gasto esencial", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "Vuelo, hotel, seguro...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = categoryEssential, onCheckedChange = onCategoryEssentialChange)
            }

            // — Botón guardar —
            Button(
                onClick = {
                    val amount = categoryAmount.toDoubleOrNull() ?: return@Button
                    val categoryToSave = editingCategory?.copy(
                        name = categoryName.trim(),
                        emoji = categoryEmoji.ifBlank { "💰" },
                        plannedAmount = amount,
                        isEssential = categoryEssential
                    ) ?: BudgetCategoryEntity(
                        tripId = tripId,
                        name = categoryName.trim(),
                        emoji = categoryEmoji.ifBlank { "💰" },
                        plannedAmount = amount,
                        isEssential = categoryEssential,
                        createdAt = System.currentTimeMillis().toString()
                    )
                    if (isEditing) onUpdateCategory(categoryToSave) else onSaveCategory(
                        categoryToSave
                    )
                    onClearForm()
                    onDismiss()
                },
                enabled = formValid,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    text = if (isEditing) "Actualizar categoría" else "Guardar categoría",
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}