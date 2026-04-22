package com.example.managemate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val btnAttendance = findViewById<Button>(R.id.btnAttendance)
        val btnCalendar = findViewById<Button>(R.id.btnCalendar)
        val btnTodo = findViewById<Button>(R.id.btnTodo)
        val btnReminder = findViewById<Button>(R.id.btnReminder)

        btnAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        btnCalendar.setOnClickListener {
            startActivity(Intent(this, CalendarActivity::class.java))
        }

        btnTodo.setOnClickListener {
            startActivity(Intent(this, TodoActivity::class.java))
        }

        btnReminder.setOnClickListener {
            startActivity(Intent(this, ReminderActivity::class.java))
        }
    }
}