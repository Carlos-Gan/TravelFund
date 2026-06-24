package com.gamo.travelfund.ui.views.screens.tripDetail

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.gamo.travelfund.data.model.entity.SavingMovementEntity

@Composable
fun MovementOptionsDialog(
    show: Boolean,
    movement: SavingMovementEntity?,
    onDismiss: () -> Unit,
    onEdit: (SavingMovementEntity) -> Unit,
    onDelete: (SavingMovementEntity) -> Unit
) {
    if (!show || movement == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Movimiento")
        },
        text = {
            Text("¿Qué quieres hacer con este movimiento?")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onEdit(movement)
                }
            ) {
                Text("Editar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDelete(movement)
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Eliminar")
            }
        }
    )
}