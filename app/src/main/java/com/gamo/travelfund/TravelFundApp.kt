package com.gamo.travelfund

import android.app.Application
import android.app.NotificationManager
import androidx.room.Room
import com.gamo.travelfund.data.local.AppDatabase
import com.gamo.travelfund.services.NotificationHelper
import com.gamo.travelfund.services.workers.WorkScheduler

class TravelFundApp : Application()  {
    lateinit var database: AppDatabase
    private set

    override fun onCreate() {
        super.onCreate()

        NotificationHelper.createChannel(this)
        WorkScheduler.scheduleTravelReminder(this)
        WorkScheduler.scheduleNoSavingsReminder(this)
        WorkScheduler.scheduleExchangeRateWorker(this)


        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "travelfund_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }
}