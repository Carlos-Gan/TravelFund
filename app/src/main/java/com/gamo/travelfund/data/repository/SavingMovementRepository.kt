package com.gamo.travelfund.data.repository

import com.gamo.travelfund.data.model.dao.SavingMovementDao
import com.gamo.travelfund.data.model.entity.SavingMovementEntity

class SavingMovementRepository(
    private val dao: SavingMovementDao
) {
    fun getMovementsForTrip(tripId: Long) = dao.getMovementsForTrip(tripId)

    suspend fun insertMovement(movement: SavingMovementEntity) = dao.insert(movement)

    suspend fun getRealSavedAmount(tripId: Long): Double {
        return dao.getRealSavedAmount(tripId)
    }

    suspend fun deleteMovement(movement: SavingMovementEntity) = dao.delete(movement)
}
