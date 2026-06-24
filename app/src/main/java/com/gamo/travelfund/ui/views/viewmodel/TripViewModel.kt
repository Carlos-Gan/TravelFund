package com.gamo.travelfund.ui.views.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gamo.travelfund.data.model.entity.TripEntity
import com.gamo.travelfund.data.repository.TripRepository
import kotlinx.coroutines.launch

class TripViewModel(
    private val repository: TripRepository
) : ViewModel() {

    val tripsWithStats = repository.tripsWithStats

    fun insertTrip(trip: TripEntity) {
        viewModelScope.launch {
            repository.insertTrip(trip)
        }
    }

    fun deleteTrip(trip: TripEntity) {
        viewModelScope.launch {
            repository.deleteTrip(trip)
        }
    }

    fun updateTrip(trip: TripEntity) {
        viewModelScope.launch {
            repository.updateTrip(trip)
        }
    }


}