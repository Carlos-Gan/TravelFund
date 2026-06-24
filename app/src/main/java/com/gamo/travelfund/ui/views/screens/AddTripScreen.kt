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
import com.gamo.travelfund.data.model.entity.TripStatus
import com.gamo.travelfund.ui.components.DatePickerField
import com.gamo.travelfund.ui.viewmodel.AddTripViewModel
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun parseDate(value: String): LocalDate? =
    runCatching { LocalDate.parse(value, DATE_FORMAT) }.getOrNull()

// UTC para consistencia con DatePickerField
private fun dateToMillis(value: String): Long =
    LocalDate.parse(value, DATE_FORMAT)
        .atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTripScreen(
    onBack: () -> Unit,
    onSubmitTrip: (TripEntity) -> Unit,
    viewModel: AddTripViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    editingTrip: TripEntity? = null
) {

    val isEditing = editingTrip != null

    val suggestions by viewModel.suggestions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var name by remember { mutableStateOf(editingTrip?.name ?: "") }
    var destination by remember { mutableStateOf(editingTrip?.destination ?: "") }
    var departureDate by remember {
        mutableStateOf(
            editingTrip?.departureDateMillis?.let { millisToDate(it) } ?: ""

        )
    }
    var returnDate by remember {
        mutableStateOf(
            editingTrip?.returnDateMillis?.let { millisToDate(it) } ?: ""
        )
    }
    var budget by remember {
        mutableStateOf(editingTrip?.totalBudget?.let {
            if (it % 1.0 == 0.0) it.toInt().toString() else it.toString()
        } ?: "")
    }
    var destinationCurrency by remember {
        mutableStateOf(
            editingTrip?.destinationCurrency ?: "USD"
        )
    }
    var expanded by remember { mutableStateOf(false) }

    val departureParsed = remember(departureDate) { parseDate(departureDate) }
    val returnParsed = remember(returnDate) { parseDate(returnDate) }

    val suggestedCurrency by viewModel.suggestedCurrency.collectAsState()

    // — Validaciones —
    val nameError = name.isNotBlank() && name.length < 3
    val budgetError = budget.isNotBlank() && budget.toDoubleOrNull() == null
    val currencyError = destinationCurrency.isNotBlank() && destinationCurrency.length < 3
    val dateError = departureParsed != null &&
            returnParsed != null &&
            !returnParsed.isAfter(departureParsed)


    val formValid = name.isNotBlank() &&
            name.length >= 3 &&
            destination.isNotBlank() &&
            departureParsed != null &&
            returnParsed != null &&
            !dateError &&
            (budget.toDoubleOrNull() ?: 0.0) > 0 &&
            destinationCurrency.length == 3

    LaunchedEffect(suggestedCurrency) {
        suggestedCurrency?.let { destinationCurrency = it }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            if (isEditing) "Editar viaje" else "Nuevo viaje",
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            if (isEditing) "Modifica los datos del viaje" else "Completa los datos del viaje",
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
                supportingText = if (nameError) ({ Text("Mínimo 3 caracteres") }) else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded && destination.isNotBlank() && suggestions.isNotEmpty(),
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = destination,
                    onValueChange = {
                        destination = it
                        viewModel.onQueryChange(it)
                        expanded = true
                    },
                    label = { Text("Destino") },
                    placeholder = { Text("Ej. Tokio") },
                    singleLine = true,
                    trailingIcon = {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expanded && destination.isNotBlank() && suggestions.isNotEmpty(),
                    onDismissRequest = { expanded = false }
                ) {
                    suggestions.forEach { city ->
                        DropdownMenuItem(
                            text = { Text(city) },
                            onClick = {
                                destination = city
                                viewModel.clearSuggestions()
                                viewModel.onCitySelected(city)
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
                    supportingText = if (budgetError) ({ Text("Número inválido") }) else null,
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = destinationCurrency,
                    onValueChange = { if (it.length <= 3) destinationCurrency = it.uppercase() },
                    label = { Text("Moneda") },
                    placeholder = { Text("USD") },
                    isError = currencyError,
                    supportingText = if (currencyError) ({ Text("3 letras") }) else null,
                    singleLine = true,
                    modifier = Modifier.width(96.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val trip = TripEntity(
                        id = editingTrip?.id ?: 0,
                        name = name,
                        destination = destination,
                        departureDateMillis = dateToMillis(departureDate),
                        returnDateMillis = dateToMillis(returnDate),
                        baseCurrency = "MXN",
                        destinationCurrency = destinationCurrency,
                        totalBudget = budget.toDoubleOrNull() ?: 0.0,
                        exchangeRate = editingTrip?.exchangeRate ?: 0.0,
                        convertedBudget = editingTrip?.convertedBudget ?: 0.0,
                        lastExchangeUpdate = editingTrip?.lastExchangeUpdate
                            ?: System.currentTimeMillis(),
                        status = editingTrip?.status ?: TripStatus.PLANNED,
                        createdAt = editingTrip?.createdAt ?: System.currentTimeMillis(),
                        coverImageUrl = editingTrip?.coverImageUrl ?: "",
                        isFinished = editingTrip?.isFinished ?: false
                    )

                    onSubmitTrip(trip)

                },
                enabled = formValid,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    if (isEditing) "Guardar cambios" else "Guardar viaje",
                    fontWeight = FontWeight.Medium
                )
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

private fun millisToDate(millis: Long): String =
    java.time.Instant.ofEpochMilli(millis)
        .atZone(java.time.ZoneOffset.UTC)
        .toLocalDate()
        .format(DATE_FORMAT)