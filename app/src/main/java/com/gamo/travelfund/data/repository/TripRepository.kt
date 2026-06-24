package com.gamo.travelfund.data.repository

import com.gamo.travelfund.data.model.dao.TripDao
import com.gamo.travelfund.data.model.entity.TripEntity

class TripRepository(
    private val tripDao: TripDao
) {

    val tripsWithStats = tripDao.getTripsWithStats()
    val trips = tripDao.getAllTrips()

    suspend fun insertTrip(trip: TripEntity) {
        tripDao.insertTrip(trip)
    }

    suspend fun updateTrip(trip: TripEntity) {
        tripDao.updateTrip(trip)
    }

    suspend fun deleteTrip(trip: TripEntity) {
        tripDao.deleteTrip(trip)
    }

    suspend fun updateExchangeRate(
        tripId: Long,
        exchangeRate: Double
    ) {
        tripDao.updateExchangeRate(tripId, exchangeRate)
    }

}
