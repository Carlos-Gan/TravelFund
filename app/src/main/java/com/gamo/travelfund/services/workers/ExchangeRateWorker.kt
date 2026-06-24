package com.gamo.travelfund.services.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gamo.travelfund.TravelFundApp
import com.gamo.travelfund.data.preferences.SettingsPreferences
import com.gamo.travelfund.data.repository.CurrencyRepository
import com.gamo.travelfund.services.NotificationHelper
import kotlinx.coroutines.flow.first
import kotlin.math.abs

class ExchangeRateWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {

        val settings = SettingsPreferences(context).settingsFlow.first()

        if (!settings.notificationsEnabled || !settings.notifyExchangeRate) {
            return Result.success()
        }

        val app = context.applicationContext as TravelFundApp

        val tripDao = app.database.tripDao()

        val trips = tripDao.getTripsOnce()

        val currencyRepository = CurrencyRepository()

        trips.forEach { trip ->

            val newRate = currencyRepository.getExchangeRate(
                baseCurrency = trip.baseCurrency,
                destinationCurrency = trip.destinationCurrency
            )

            if (trip.exchangeRate > 0.0) {

                val change =
                    abs(newRate - trip.exchangeRate) / trip.exchangeRate * 100

                if (change >= 5) {

                    NotificationHelper.showNotification(
                        context,
                        "Tipo de cambio actualizado 💱",
                        "El tipo de cambio para ${trip.destination} cambió ${"%.1f".format(change)}%."
                    )
                }
            }

            tripDao.updateExchangeRate(
                tripId = trip.id,
                exchangeRate = newRate
            )
        }

        return Result.success()
    }
}