package com.gamo.travelfund.data.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class MovementType {INCOME, EXPENSE}

@Entity(
    tableName = "saving_movements",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = BudgetCategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("tripId"),
        Index("categoryId")
    ]
)
data class SavingMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val categoryId: Long? = null,
    val amount: Double,
    val type: MovementType,
    val note: String = "",
    val dateMillis: Long = System.currentTimeMillis()
)
