package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.gamo.travelfund.data.model.entity.BudgetCategoryEntity

@Composable
fun CategoryOptionsDialog(
    show: Boolean,
    category: BudgetCategoryEntity?,
    onDismiss: () -> Unit,
    onEdit: (BudgetCategoryEntity) -> Unit,
    onDelete: (BudgetCategoryEntity) -> Unit
) {
    if (!show || category == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Categoría")
        },
        text = {
            Text("¿Qué quieres hacer con esta categoría?")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEdit(category)
                }
            ) {
                Text("Editar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDelete(category)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
            ) {
                Text("Eliminar")
            }
        }
    )
}