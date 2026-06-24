package com.gamo.travelfund.data.repository

import com.gamo.travelfund.data.remote.RetrofitInstance

class CurrencyRepository {
    private val api = RetrofitInstance.currencyApi

    suspend fun getExchangeRate(
        baseCurrency: String,
        destinationCurrency: String
    ): Double {
        val response = api.getRates(baseCurrency)

        return response.rates[destinationCurrency.uppercase()] ?: 1.0
    }

}