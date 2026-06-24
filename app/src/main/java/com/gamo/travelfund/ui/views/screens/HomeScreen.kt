package com.gamo.travelfund.ui.views.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.data.model.entity.TripStatus
import com.gamo.travelfund.data.stats.TripWithStats
import com.gamo.travelfund.ui.components.TripCard

enum class TripFilter(val label: String) {
    TODOS("Todos"),
    ACTIVOS("Activos"),
    TERMINADOS("Terminados")
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
            text = { Text("¿Qué quieres hacer con este viaje?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOptionsDialog = false
                        onEditTrip(selectedTrip!!)
                        selectedTrip = null
                    }
                ) {
                    Text("Editar")
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
                    Text("Eliminar")
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
            title = { Text("Eliminar viaje") },
            text = {
                Text("¿Estás seguro de que quieres eliminar \"${selectedTrip!!.name}\"? Esta acción no se puede deshacer.")
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
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        selectedTrip = null
                    }
                ) {
                    Text("Cancelar")
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
                Icon(Icons.Default.Add, contentDescription = "Agregar viaje")
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
                        text = "Mis viajes",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Medium
                    )
                    if (trips.isNotEmpty()) {
                        Spacer(Modifier.height(2.dp))
                        Text(
                            text = "${trips.size} viaje${if (trips.size != 1) "s" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onSettingsClick) {
                    Icon(Icons.Default.ClearAll, contentDescription = "Configuración")
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
                            label = { Text(filter.label) },
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
                    title = "Sin viajes todavía",
                    subtitle = "Empieza a planear tu próxima aventura y lleva el seguimiento de tus ahorros."
                )

                // Hay viajes pero el filtro no muestra ninguno
                filteredTrips.isEmpty() -> EmptyState(
                    emoji = "🔍",
                    title = "Sin resultados",
                    subtitle = "No tienes viajes en esta categoría todavía."
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