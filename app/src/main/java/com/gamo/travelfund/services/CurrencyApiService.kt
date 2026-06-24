package com.gamo.travelfund.services

import retrofit2.http.GET
import retrofit2.http.Path

interface CurrencyApiService {
    @GET("v6/latest/{baseCurrency}")
    suspend fun getRates(
        @Path("baseCurrency") baseCurrency: String
    ): ExchangeRateResponse
}