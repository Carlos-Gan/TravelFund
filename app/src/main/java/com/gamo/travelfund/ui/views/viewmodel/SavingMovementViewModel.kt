package com.gamo.travelfund.ui.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.repository.SavingMovementRepository
import kotlinx.coroutines.launch

class SavingMovementViewModel(
    private val savingRepository: SavingMovementRepository,
) : ViewModel() {
    fun getMovementsByTrip(tripId: Long) =
        savingRepository.getMovementsForTrip(tripId)

    fun insertMovement(
        movement: SavingMovementEntity,
        onAfterInsert: suspend () -> Unit = {}
    ) {
        viewModelScope.launch {
            savingRepository.insertMovement(movement)
            onAfterInsert()
        }
    }

    fun deleteMovement(movement: SavingMovementEntity) {
        viewModelScope.launch {
            savingRepository.deleteMovement(movement)
        }
    }

    fun updateMovement(movement: SavingMovementEntity) {
        viewModelScope.launch {
            savingRepository.updateMovement(movement)
        }
    }

    suspend fun getRealSavedAmount(tripId: Long): Double {
        return savingRepository.getRealSavedAmount(tripId)
    }
}