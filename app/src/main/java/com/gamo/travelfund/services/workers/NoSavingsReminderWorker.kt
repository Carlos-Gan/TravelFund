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

class NoSavingsReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {

        val settings = SettingsPreferences(context).settingsFlow.first()

        if (!settings.notificationsEnabled || !settings.notifyNoSavings) {
            return Result.success()
        }

        val app = context.applicationContext as TravelFundApp

        val trips = app.database.tripDao().getTripsOnce()

        val dao = app.database.savingMovementDao()

        val today = LocalDate.now()

        trips.forEach { trip ->

            val lastIncome = dao.getLastIncomeMovement(trip.id)

            if (lastIncome != null) {

                val lastDate = Instant.ofEpochMilli(lastIncome.dateMillis)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                val days = ChronoUnit.DAYS.between(lastDate, today)

                val reminderDays = listOf(7L, 12L, 25L, 30L)

                if (days in reminderDays) {
                    NotificationHelper.showNotification(
                        context,
                        "No has ahorrado recientemente 💰",
                        "Han pasado $days días desde tu último ahorro para ${trip.name}."
                    )
                }
            }
        }

        return Result.success()
    }
}