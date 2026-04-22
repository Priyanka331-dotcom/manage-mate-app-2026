
package com.example.managemate

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class ReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)

        if (android.os.Build.VERSION.SDK_INT >= 33) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }

        val btnNotify = findViewById<Button>(R.id.btnNotify)


        btnNotify.setOnClickListener {
            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()
            pickDateTime()
        }
    }

    private fun pickDateTime() {
        val calendar = Calendar.getInstance()



            DatePickerDialog(this, { _, year, month, day ->

                Toast.makeText(this, "Date Selected", Toast.LENGTH_SHORT).show()


                    TimePickerDialog(this, { _, hour, minute ->

                        Toast.makeText(this, "Time Selected", Toast.LENGTH_SHORT).show()

                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, day, hour, minute, 0)

                val timeInMillis = selectedCalendar.timeInMillis

                // ❗ future time check
                if (timeInMillis < System.currentTimeMillis()) {
                    Toast.makeText(this, "Select future time!", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }

                scheduleReminder(timeInMillis)

            },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
            ).show()

        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun scheduleReminder(timeInMillis: Long) {

        Toast.makeText(this, "Inside scheduleReminder", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            timeInMillis,
            pendingIntent
        )

        Toast.makeText(this, "Alarm Scheduled!", Toast.LENGTH_SHORT).show()

    }
}