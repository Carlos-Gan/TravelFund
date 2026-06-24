package com.gamo.travelfund.services.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gamo.travelfund.TravelFundApp
import com.gamo.travelfund.data.preferences.SettingsPreferences
import com.gamo.travelfund.services.NotificationHelper
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TravelReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val settings = SettingsPreferences(context).settingsFlow.first()

        if (!settings.notificationsEnabled || !settings.notifyFewDays) {
            return Result.success()
        }

        val app = context.applicationContext as TravelFundApp
        val trips = app.database.tripDao().getTripsOnce()

        val today = LocalDate.now()
        val reminderDays = listOf(30L, 15L, 7L, 5L, 3L, 2L, 1L)

        trips.forEach { trip ->
            val departureDate = Instant.ofEpochMilli(trip.departureDateMillis)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            val daysRemaining = ChronoUnit.DAYS.between(today, departureDate)

            if (daysRemaining in reminderDays) {
                NotificationHelper.showNotification(
                    context,
                    "Viaje próximo ✈️",
                    "Faltan $daysRemaining días para ${trip.name}"
                )
            }
        }

        return Result.success()
    }
}