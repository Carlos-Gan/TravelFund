package com.gamo.travelfund.ui.views.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.R
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.data.model.entity.TripStatus
import com.gamo.travelfund.data.stats.TripWithStats
import com.gamo.travelfund.ui.components.TripCard

enum class TripFilter(
    @StringRes val labelRes: Int
) {
    TODOS(R.string.todos),
    ACTIVOS(R.string.activos),
    TERMINADOS(R.string.terminados)
}

@Composable
fun HomeScreen(
    trips: List<TripWithStats>,
    onAddTrip: () -> Unit = {},
    onTripClick: (TripEntity) -> Unit = {},
    onDeleteTrip: (TripEntity) -> Unit = {},
    onEditTrip: (TripEntity) -> Unit = {},
    onSettingsClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf(TripFilter.TODOS) }

    val filteredTrips = remember(trips, selectedFilter) {
        when (selectedFilter) {
            TripFilter.TODOS -> trips
            TripFilter.ACTIVOS -> trips.filter {
                it.trip.status == TripStatus.PLANNED || it.trip.status == TripStatus.ACTIVE
            }

            TripFilter.TERMINADOS -> trips.filter {
                it.trip.status == TripStatus.FINISHED || it.trip.status == TripStatus.CANCELLED
            }
        }
    }

    var selectedTrip by remember { mutableStateOf<TripEntity?>(null) }
    var showOptionsDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showOptionsDialog && selectedTrip != null) {
        AlertDialog(
            onDismissRequest = {
                showOptionsDialog = false
                selectedTrip = null
            },
            title = { Text(selectedTrip!!.name, fontWeight = FontWeight.Medium) },
            text = { Text(stringResource(R.string.qu_quieres_hacer_con_este_viaje)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOptionsDialog = false
                        onEditTrip(selectedTrip!!)
                        selectedTrip = null
                    }
                ) {
                    Text(stringResource(R.string.editar))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showOptionsDialog = false
                        showDeleteDialog = true  // pasa al dialog de confirmación
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.eliminar))
                }
            }
        )
    }

    // — Dialog de confirmación de borrado —
    if (showDeleteDialog && selectedTrip != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                selectedTrip = null
            },
            title = { Text(stringResource(R.string.eliminar_viaje)) },
            text = {
                Text("${stringResource(R.string.est_s_seguro_de_que_quieres_eliminar)} \"${selectedTrip!!.name}\"? ${
                    stringResource(
                        R.string.esta_acci_n_no_se_puede_deshacer
                    )}")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTrip(selectedTrip!!)
                        showDeleteDialog = false
                        selectedTrip = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(stringResource(R.string.eliminar))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedTrip = null
                    }
                ) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTrip,
                shape = RoundedCornerShape(16.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.agregar_viaje))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // — Header —
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.mis_viajes),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (trips.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = stringResource(
                                R.string.viaje,
                                trips.size,
                                if (trips.size != 1) "s" else ""
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.ClearAll, contentDescription = stringResource(R.string.configuracion))
                }
            }

            // — Filtros —
            if (trips.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(TripFilter.entries) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(stringResource(filter.labelRes)) },
                            leadingIcon = if (selectedFilter == filter) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null,
                                        modifier = Modifier.size(FilterChipDefaults.IconSize)
                                    )
                                }
                            } else null
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // — Contenido —
            when {
                // No hay viajes en absoluto
                trips.isEmpty() -> EmptyState(
                    emoji = "✈️",
                    title = stringResource(R.string.sin_viajes_todav_a),
                    subtitle = stringResource(R.string.empieza_a_planear_tu_pr_xima_aventura_y_lleva_el_seguimiento_de_tus_ahorros)
                )

                // Hay viajes pero el filtro no muestra ninguno
                filteredTrips.isEmpty() -> EmptyState(
                    emoji = "🔍",
                    title = stringResource(R.string.sin_resultados),
                    subtitle = stringResource(R.string.no_tienes_viajes_en_esta_categoria_todavia)
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredTrips, key = { it.trip.id }) { tripWithStats ->
                        TripCard(
                            trip = tripWithStats.trip,
                            onClick = { onTripClick(tripWithStats.trip) },
                            onLongClick = {
                                selectedTrip = tripWithStats.trip
                                showOptionsDialog = true
                            },
                            savedAmount = tripWithStats.savedAmount
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyState(emoji: String, title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}