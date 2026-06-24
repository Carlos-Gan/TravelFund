package com.gamo.travelfund.ui.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.repository.CurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CurrencyViewModel(
    private val repository: CurrencyRepository
) : ViewModel() {

    private val _exchangeRate = MutableStateFlow(1.0)
    val exchangeRate: StateFlow<Double> = _exchangeRate

    fun fetchExchangeRate(
        baseCurrency: String,
        destinationCurrency: String
    ){
        viewModelScope.launch {
            try {
                val rate = repository.getExchangeRate(
                    baseCurrency=baseCurrency,
                    destinationCurrency=destinationCurrency
                )

                _exchangeRate.value= if (rate>0.0) rate else 1.0

            }catch (e:Exception){
                e.printStackTrace()
                _exchangeRate.value=1.0
            }
        }
    }
}