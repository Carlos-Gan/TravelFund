package com.gamo.travelfund.ui.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.R
import com.gamo.travelfund.data.preferences.NotificationSettings

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
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            MediumTopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.configuracion), fontWeight = FontWeight.Medium)
                        Text(
                            stringResource(R.string.personaliza_tu_experiencia),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // — Sección principal de notificaciones —
            SettingsSection(label = stringResource(R.string.notificaciones)) {

                // Switch maestro con estilo destacado
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (settings.notificationsEnabled)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.activar_notificaciones),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (settings.notificationsEnabled)
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = stringResource(R.string.recibir_recordatorios_sobre_tus_viajes_y_ahorros),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (settings.notificationsEnabled)
                                    MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f)
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                            )
                        }
                        Switch(
                            checked = settings.notificationsEnabled,
                            onCheckedChange = onNotificationsEnabledChange
                        )
                    }
                }

                // Sub-opciones — solo visibles si las notificaciones están activas
                if (settings.notificationsEnabled) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            0.5.dp, MaterialTheme.colorScheme.outlineVariant
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column {
                            SettingsSwitchItem(
                                emoji = "📅",
                                title = stringResource(R.string.pocos_d_as_restantes),
                                subtitle = stringResource(R.string.avisar_cuando_falten_pocos_d_as_para_el_viaje),
                                checked = settings.notifyFewDays,
                                onCheckedChange = onNotifyFewDaysChange
                            )
                            SettingsDivider()
                            SettingsSwitchItem(
                                emoji = "🎯",
                                title = stringResource(R.string.meta_de_ahorro),
                                subtitle = stringResource(R.string.avisar_al_alcanzar_50_75_o_100),
                                checked = settings.notifySavingGoal,
                                onCheckedChange = onNotifySavingGoalChange
                            )
                            SettingsDivider()
                            SettingsSwitchItem(
                                emoji = "💤",
                                title = stringResource(R.string.sin_ahorrar_recientemente),
                                subtitle = stringResource(R.string.avisar_si_llevas_d_as_sin_registrar_ahorro),
                                checked = settings.notifyNoSavings,
                                onCheckedChange = onNotifyNoSavingsChange
                            )
                            SettingsDivider()
                            SettingsSwitchItem(
                                emoji = "💱",
                                title = stringResource(R.string.tipo_de_cambio),
                                subtitle = stringResource(R.string.avisar_si_cambia_mucho_la_moneda_del_destino),
                                checked = settings.notifyExchangeRate,
                                onCheckedChange = onNotifyExchangeRateChange
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// — Sección con label —
@Composable
private fun SettingsSection(
    label: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 4.dp)
        )
        content()
    }
}

// — Item individual dentro de la card —
@Composable
private fun SettingsSwitchItem(
    emoji: String,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(emoji, style = MaterialTheme.typography.titleMedium)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

// — Divider interno de la card —
@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.outlineVariant
    )
}