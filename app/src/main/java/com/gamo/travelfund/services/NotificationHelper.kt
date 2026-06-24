package com.gamo.travelfund.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.gamo.travelfund.R

object NotificationHelper {
    const val CHANNEL_ID = "travelfund_notifications"

    fun createChannel(context: Context) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.app_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )

        val manager = context.getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(channel)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(
        context: Context,
        title: String,
        message: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED

        ) {
            return
        }
        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }

    }
}