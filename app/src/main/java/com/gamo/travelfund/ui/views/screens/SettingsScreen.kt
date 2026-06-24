package com.gamo.travelfund.ui.views.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.gamo.travelfund.data.preferences.NotificationSettings
import com.gamo.travelfund.services.NotificationHelper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: NotificationSettings,
    onBack: () -> Unit,
    onNotificationsEnabledChange: (Boolean) -> Unit,
    onNotifyFewDaysChange: (Boolean) -> Unit,
    onNotifySavingGoalChange: (Boolean) -> Unit,
    onNotifyNoSavingsChange: (Boolean) -> Unit,
    onNotifyExchangeRateChange: (Boolean) -> Unit
) {

    val context = LocalContext.current

    Scaffold(
        topBar = {
            MediumTopAppBar(
                title = {
                    Text("Configuración")
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )

            SettingsSwitchItem(
                title = "Activar notificaciones",
                subtitle = "Recibir recordatorios sobre tus viajes y ahorros",
                checked = settings.notificationsEnabled,
                onCheckedChange = onNotificationsEnabledChange
            )

            if (settings.notificationsEnabled) {
                SettingsSwitchItem(
                    title = "Pocos días restantes",
                    subtitle = "Avisar cuando falten pocos días para el viaje",
                    checked = settings.notifyFewDays,
                    onCheckedChange = onNotifyFewDaysChange
                )

                SettingsSwitchItem(
                    title = "Meta de ahorro",
                    subtitle = "Avisar cuando alcances 50%, 75% o 100%",
                    checked = settings.notifySavingGoal,
                    onCheckedChange = onNotifySavingGoalChange
                )

                SettingsSwitchItem(
                    title = "Sin ahorrar recientemente",
                    subtitle = "Avisar si llevas varios días sin registrar ahorro",
                    checked = settings.notifyNoSavings,
                    onCheckedChange = onNotifyNoSavingsChange
                )

                SettingsSwitchItem(
                    title = "Tipo de cambio",
                    subtitle = "Avisar si cambia mucho la moneda del destino",
                    checked = settings.notifyExchangeRate,
                    onCheckedChange = onNotifyExchangeRateChange
                )
            }
        }
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(title, fontWeight = FontWeight.Medium)
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )

        }
    }
}
