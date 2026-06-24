package com.gamo.travelfund.data.remote

import com.gamo.travelfund.services.NominatimApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NominatimClient {
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "TravelFund/1.0")
                .build()
            chain.proceed(request)
        }
        .build()

    val api: NominatimApiService = Retrofit.Builder()
        .baseUrl("https://nominatim.openstreetmap.org/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(NominatimApiService::class.java)
}