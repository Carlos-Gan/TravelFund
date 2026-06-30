package com.gamo.travelfund.services.workers

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gamo.travelfund.R
import com.gamo.travelfund.TravelFundApp
import com.gamo.travelfund.data.preferences.SettingsPreferences
import com.gamo.travelfund.services.NotificationHelper
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class TravelReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        return try {
            val settings = SettingsPreferences(applicationContext)
                .settingsFlow
                .first()

            if (!settings.notificationsEnabled || !settings.notifyFewDays) {
                return Result.success()
            }

            if (!hasNotificationPermission()) {
                return Result.success()
            }

            val app = applicationContext as? TravelFundApp
                ?: return Result.failure()

            val trips = app.database.tripDao().getTripsOnce()

            val zoneId = ZoneId.systemDefault()
            val today = LocalDate.now(zoneId)

            trips.forEach { trip ->
                val departureDate = Instant
                    .ofEpochMilli(trip.departureDateMillis)
                    .atZone(zoneId)
                    .toLocalDate()

                val daysRemaining = ChronoUnit.DAYS.between(
                    today,
                    departureDate
                )

                if (daysRemaining !in REMINDER_DAYS) {
                    return@forEach
                }

                val reminder = buildReminderContent(
                    daysRemaining = daysRemaining
                )

                val messageWithTripName = buildString {
                    append(trip.name)
                    append("\n")
                    append(reminder.message)
                }

                NotificationHelper.showNotification(
                    context = applicationContext,
                    title = reminder.title,
                    message = messageWithTripName
                )
            }

            Result.success()
        } catch (exception: Exception) {
            Result.retry()
        }
    }

    private fun hasNotificationPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }

        return ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun buildReminderContent(daysRemaining: Long): ReminderContent {
        return when (daysRemaining) {
            60L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_60_title),
                message = applicationContext.getString(R.string.reminder_60_message)
            )

            30L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_30_title),
                message = applicationContext.getString(R.string.reminder_30_message)
            )

            15L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_15_title),
                message = applicationContext.getString(R.string.reminder_15_message)
            )

            7L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_7_title),
                message = applicationContext.getString(R.string.reminder_7_message)
            )

            5L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_5_title),
                message = applicationContext.getString(R.string.reminder_5_message)
            )

            3L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_3_title),
                message = applicationContext.getString(R.string.reminder_3_message)
            )

            2L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_2_title),
                message = applicationContext.getString(R.string.reminder_2_message)
            )

            1L -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_1_title),
                message = applicationContext.getString(R.string.reminder_1_message)
            )

            else -> ReminderContent(
                title = applicationContext.getString(R.string.reminder_default_title),
                message = applicationContext.getString(R.string.reminder_default_message)
            )
        }
    }

    private data class ReminderContent(
        val title: String,
        val message: String
    )

    private companion object {
        val REMINDER_DAYS = setOf(
            60L,
            30L,
            15L,
            7L,
            5L,
            3L,
            2L,
            1L
        )
    }
}