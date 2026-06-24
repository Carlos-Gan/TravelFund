package com.gamo.travelfund.ui.views.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.ui.components.DatePickerField
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun parseDate(value: String): LocalDate? =
    runCatching { LocalDate.parse(value, DATE_FORMAT) }.getOrNull()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(
    onBack: () -> Unit,
    onSaveTrip: (TripEntity) -> Unit
) {
    val citySuggestions = listOf(
        "Los Angeles, Estados Unidos",
        "Los Cabos, México",
        "Las Vegas, Estados Unidos",
        "Londres, Reino Unido",
        "Tokio, Japón",
        "París, Francia",
        "Madrid, España",
        "Roma, Italia",
        "Cancún, México",
        "Monterrey, México"
    )

    var name               by remember { mutableStateOf("") }
    var destination        by remember { mutableStateOf("") }
    var departureDate      by remember { mutableStateOf("") }
    var returnDate         by remember { mutableStateOf("") }
    var budget             by remember { mutableStateOf("") }
    var destinationCurrency by remember { mutableStateOf("USD") }
    var expanded           by remember { mutableStateOf(false) }

    val departureParsed = remember(departureDate) { parseDate(departureDate) }
    val returnParsed    = remember(returnDate)    { parseDate(returnDate) }

    val nameError   = name.isNotBlank() && name.length < 3
    val budgetError = budget.isNotBlank() && budget.toDoubleOrNull() == null
    val dateError   = departureParsed != null &&
            returnParsed != null &&
            !returnParsed.isAfter(departureParsed)

    val filteredCities = citySuggestions.filter {
        it.contains(destination, ignoreCase = true)
    }

    val formValid = name.isNotBlank() &&
            name.length >= 3 &&
            destination.isNotBlank() &&
            departureParsed != null &&
            returnParsed != null &&
            !dateError &&
            (budget.toDoubleOrNull() ?: 0.0) > 0 &&
            destinationCurrency.length == 3

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Nuevo viaje", fontWeight = FontWeight.Medium)
                        Text(
                            "Completa los datos del viaje",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            SectionLabel("Información del viaje")

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del viaje") },
                placeholder = { Text("Ej. Tokio 2026") },
                isError = nameError,
                supportingText = if (nameError) {
                    { Text("Mínimo 3 caracteres") }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded && destination.isNotBlank() && filteredCities.isNotEmpty(),
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = destination,
                    onValueChange = {
                        destination = it
                        expanded = true
                    },
                    label = { Text("Destino") },
                    placeholder = { Text("Ej. Los Angeles") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded && destination.isNotBlank() && filteredCities.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    filteredCities.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                destination = city
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            SectionLabel("Fechas")

            DatePickerField(
                label = "Fecha de salida",
                value = departureDate,
                onDateSelected = { departureDate = it }
            )

            DatePickerField(
                label = "Fecha de regreso",
                value = returnDate,
                onDateSelected = { returnDate = it }
            )

            // Error de fechas — se muestra solo cuando ambas están llenas y son inválidas
            if (dateError) {
                Text(
                    text = "La fecha de regreso debe ser después de la salida",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(start = 16.dp, top = 2.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
            SectionLabel("Presupuesto")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    label = { Text("Presupuesto total") },
                    prefix = { Text("$") },
                    isError = budgetError,
                    supportingText = if (budgetError) {
                        { Text("Número inválido") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = destinationCurrency,
                    onValueChange = { if (it.length <= 3) destinationCurrency = it.uppercase() },
                    label = { Text("Moneda") },
                    placeholder = { Text("USD") },
                    singleLine = true,
                    modifier = Modifier.width(96.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val trip = TripEntity(
                        name = name.trim(),
                        destination = destination.trim(),
                        departureDateMillis = dateToMillis(departureDate),
                        returnDateMillis = dateToMillis(returnDate),
                        totalBudget = budget.toDouble(),
                        destinationCurrency = destinationCurrency
                    )
                    onSaveTrip(trip)
                },
                enabled = formValid,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Guardar viaje", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

private fun dateToMillis(value: String): Long =
    LocalDate.parse(value, DATE_FORMAT)
        .atStartOfDay(java.time.ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()