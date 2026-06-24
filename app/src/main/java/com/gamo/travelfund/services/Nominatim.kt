package com.gamo.travelfund.services

import com.google.gson.annotations.SerializedName

data class NominatimPlace(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("address") val address: NominatimAddress
)

data class NominatimAddress(
    @SerializedName("village") val village: String?,
    @SerializedName("town") val town: String?,
    @SerializedName("city") val city: String?,
    @SerializedName("state") val state: String?,
    @SerializedName("state_district")   val stateDistrict: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("country_code") val countryCode: String?,
) {
    val cityName: String
        get() = city ?: town ?: village ?: ""

    val formattedAddress: String
        get(){
            val parts = listOfNotNull(
                cityName.takeIf { it.isNotBlank() },
                (state ?: stateDistrict)?.takeIf { it != cityName },
                country
            )
            return parts.joinToString ( ", " )
        }
}
