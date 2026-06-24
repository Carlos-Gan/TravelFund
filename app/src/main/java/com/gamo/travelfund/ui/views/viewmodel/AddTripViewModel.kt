package com.gamo.travelfund.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.remote.NominatimClient
import com.gamo.travelfund.services.CurrencyByCountry
import com.gamo.travelfund.services.NominatimPlace
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class AddTripViewModel : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _suggestions = MutableStateFlow<List<String>>(emptyList())
    val suggestions: StateFlow<List<String>> = _suggestions

    private val _isLoading = MutableStateFlow(false)

    private val _suggestedCurrency = MutableStateFlow<String?>(null)
    val suggestedCurrency: StateFlow<String?> = _suggestedCurrency

    // guarda los lugares crudos para poder acceder al countryCode al seleccionar
    private val _rawPlaces = MutableStateFlow<List<NominatimPlace>>(emptyList())

    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        _query
            .debounce(400)              // espera 400ms después del último caracter
            .filter { it.length >= 3 }  // mínimo 3 letras antes de buscar
            .distinctUntilChanged()
            .onEach { searchCities(it) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        _query.value = value
        if (value.length < 3) _suggestions.value = emptyList()
    }

    private fun searchCities(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                NominatimClient.api.searchCities(query)
            }.onSuccess { places ->
                _rawPlaces.value = places
                _suggestions.value = places
                    .map { it.address.formattedAddress }
                    .filter { it.isNotBlank() }
                    .distinct()
            }.onFailure {
                _suggestions.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }

    fun onCitySelected(formattedAddress: String) {
        // busca el lugar cuyo formattedAddress coincida y extrae su moneda
        val place = _rawPlaces.value.firstOrNull {
            it.address.formattedAddress == formattedAddress
        }
        _suggestedCurrency.value = CurrencyByCountry.fromCountryCode(place?.address?.countryCode)
        clearSuggestions()
    }

}