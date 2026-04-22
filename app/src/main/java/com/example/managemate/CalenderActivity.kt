package com.example.managemate

import android.app.*
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var eventName: EditText
    private lateinit var datePicker: DatePicker
    private lateinit var timePicker: TimePicker
    private lateinit var setReminderBtn: Button
    private lateinit var resultText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        eventName = findViewById(R.id.eventName)
        datePicker = findViewById(R.id.datePicker)
        timePicker = findViewById(R.id.timePicker)
        setReminderBtn = findViewById(R.id.setReminderBtn)
        resultText = findViewById(R.id.resultText)

        val sharedPref = getSharedPreferences("events", MODE_PRIVATE)

        // 🔹 Load saved event
        val savedEvent = sharedPref.getString("eventName", "")
        val savedDate = sharedPref.getString("eventDate", "")

        if (!savedEvent.isNullOrEmpty()) {
            resultText.text = "$savedEvent on $savedDate"
        }

        setReminderBtn.setOnClickListener {
            saveEvent()
            scheduleReminder()
        }
    }

    private fun saveEvent() {
        val name = eventName.text.toString()

        val day = datePicker.dayOfMonth
        val month = datePicker.month + 1
        val year = datePicker.year

        val date = "$day/$month/$year"

        val sharedPref = getSharedPreferences("events", MODE_PRIVATE)
        val editor = sharedPref.edit()

        editor.putString("eventName", name)
        editor.putString("eventDate", date)
        editor.apply()

        resultText.text = "$name on $date"
    }

    private fun scheduleReminder() {

        val name = eventName.text.toString()

        val calendar = Calendar.getInstance()

        calendar.set(
            datePicker.year,
            datePicker.month,
            datePicker.dayOfMonth,
            timePicker.hour,
            timePicker.minute,
            0
        )

        val intent = Intent(this, ReminderReceiver::class.java)
        intent.putExtra("eventName", name)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

        Toast.makeText(this, "Reminder Set!", Toast.LENGTH_SHORT).show()
    }
}