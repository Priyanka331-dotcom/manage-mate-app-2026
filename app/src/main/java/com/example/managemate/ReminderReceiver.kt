
package com.example.managemate

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        android.widget.Toast.makeText(context, "Receiver Triggered!", android.widget.Toast.LENGTH_LONG).show()
        NotificationHelper.sendNotification(context)
    }
}