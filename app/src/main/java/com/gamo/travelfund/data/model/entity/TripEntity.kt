package com.gamo.travelfund.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val destination: String,
    val departureDateMillis: Long,
    val returnDateMillis: Long,
    val baseCurrency: String = "MXN",
    val destinationCurrency: String = "USD",
    val totalBudget: Double,
    val exchangeRate: Double = 0.0,
    val convertedBudget: Double = 0.0,
    val lastExchangeUpdate: Long = System.currentTimeMillis(),
    val status: TripStatus = TripStatus.PLANNED,
    val createdAt: Long = System.currentTimeMillis(),
    val coverImageUrl: String = "",
    val isFinished: Boolean = false
)

enum class TripStatus {PLANNED, ACTIVE, FINISHED, CANCELLED}