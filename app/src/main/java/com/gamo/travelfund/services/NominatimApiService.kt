package com.gamo.travelfund.services

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApiService {
    @GET("search")
    suspend fun searchCities(
        @Query("q") query: String,
        @Query("featureClass")   featureClass: String = "P",   // P = populated places
        @Query("featureCode")    featureCode: String  = "PPLA", // ciudades principales
        @Query("format")         format: String       = "json",
        @Query("addressdetails") addressDetails: Int  = 1,
        @Query("limit")          limit: Int           = 8,
        @Query("accept-language") lang: String        = "es"
    ): List<NominatimPlace>
}