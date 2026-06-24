package com.gamo.travelfund.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SavingMovementDao {
    @Query("""
        SELECT *
        FROM saving_movements
        WHERE tripId = :tripId
        ORDER BY dateMillis DESC
    """)
    fun getMovementsForTrip(
        tripId: Long
    ): Flow<List<SavingMovementEntity>>

    @Query("""
    SELECT COALESCE(
        SUM(
            CASE 
                WHEN type = 'INCOME' THEN amount
                WHEN type = 'EXPENSE' THEN -amount
                ELSE 0
            END
        ), 0
    )
    FROM saving_movements
    WHERE tripId = :tripId
""")
    suspend fun getRealSavedAmount(tripId: Long): Double

    @Insert
    suspend fun insert(movement: SavingMovementEntity)

    @Delete
    suspend fun delete(movement: SavingMovementEntity)
}