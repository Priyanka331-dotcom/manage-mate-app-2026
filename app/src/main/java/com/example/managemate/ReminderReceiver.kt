package com.example.managemate

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val eventName = intent.getStringExtra("eventName")

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "reminder_channel"

        val channel = NotificationChannel(
            channelId,
            "Reminders",
            NotificationManager.IMPORTANCE_HIGH
        )

        manager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Reminder")
            .setContentText(eventName)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(1, notification)
    }
}