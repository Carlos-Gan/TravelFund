package com.gamo.travelfund.services.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object WorkScheduler {

    fun scheduleTravelReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<TravelReminderWorker>(
            1,
            TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "travel_reminder_worker",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun scheduleNoSavingsReminder(context: Context) {

        val request =
            PeriodicWorkRequestBuilder<NoSavingsReminderWorker>(
                1,
                TimeUnit.DAYS
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "no_savings_worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }

    fun scheduleExchangeRateWorker(context: Context) {

        val request =
            PeriodicWorkRequestBuilder<ExchangeRateWorker>(
                1,
                TimeUnit.DAYS
            ).build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "exchange_rate_worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
    }
}