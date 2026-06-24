package com.gamo.travelfund.ui.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.model.entity.SavingMovementEntity
import com.gamo.travelfund.data.repository.SavingMovementRepository
import kotlinx.coroutines.launch

class SavingMovementViewModel(
    private val savingRepository: SavingMovementRepository

) : ViewModel() {

    fun getMovementsByTrip(tripId: Long) =
        savingRepository.getMovementsForTrip(tripId)

    fun insertMovement(movement: SavingMovementEntity) {
        viewModelScope.launch {
            savingRepository.insertMovement(movement)

            val total = savingRepository.getRealSavedAmount(movement.tripId)

            println("DEBUG tripId: ${movement.tripId}")
            println("DEBUG total: $total")
        }
    }
}