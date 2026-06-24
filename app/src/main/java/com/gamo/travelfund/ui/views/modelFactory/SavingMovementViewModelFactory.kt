package com.gamo.travelfund.ui.views.modelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gamo.travelfund.data.repository.SavingMovementRepository
import com.gamo.travelfund.data.repository.TripRepository
import com.gamo.travelfund.ui.views.viewmodel.SavingMovementViewModel

class SavingMovementViewModelFactory(
    private val savingRepository: SavingMovementRepository,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SavingMovementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SavingMovementViewModel(
                savingRepository = savingRepository
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}
