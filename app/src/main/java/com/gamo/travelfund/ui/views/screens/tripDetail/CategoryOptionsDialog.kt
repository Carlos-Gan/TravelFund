package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.gamo.travelfund.R
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
            Text(stringResource(R.string.categor_a))
        },
        text = {
            Text(stringResource(R.string.qu_quieres_hacer_con_esta_categor_a))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEdit(category)
                }
            ) {
                Text(stringResource(R.string.editar))
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
                Text(stringResource(R.string.eliminar))
            }
        }
    )
}