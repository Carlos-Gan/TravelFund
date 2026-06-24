package com.gamo.travelfund.data.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.ui.components.TripWithStats
import kotlinx.coroutines.flow.Flow

@Dao
interface TripDao {
    @Query("SELECT * FROM trips ORDER BY id DESC")
    fun getAllTrips(): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE id = :tripId LIMIT 1")
    fun getTripById(tripId: Long): Flow<TripEntity?>

    @Insert
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("""
    SELECT 
        t.*,
        COALESCE(
            (
                SELECT SUM(
                    CASE 
                        WHEN sm.type = 'INCOME' THEN sm.amount
                        WHEN sm.type = 'EXPENSE' THEN -sm.amount
                        ELSE 0
                    END
                )
                FROM saving_movements sm
                WHERE sm.tripId = t.id
            ), 0
        ) AS savedAmount
    FROM trips t
    ORDER BY t.id DESC
""")
    fun getTripsWithStats(): Flow<List<TripWithStats>>
}