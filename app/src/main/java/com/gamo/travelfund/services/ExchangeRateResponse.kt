package com.gamo.travelfund.services

data class ExchangeRateResponse(
    val result: String,
    val provider: String?,
    val documentation: String?,
    val termsOfUse: String?,
    val timeLastUpdateUnix: Long?,
    val timeLastUpdateUtc: String?,
    val timeNextUpdateUnix: Long?,
    val timeNextUpdateUtc: String?,
    val baseCode: String,
    val rates: Map<String, Double>
)