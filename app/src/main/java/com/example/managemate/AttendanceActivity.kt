package com.example.managemate

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AttendanceActivity : AppCompatActivity() {

    var present = 0
    var total = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_attendance)

        val txtAttendance = findViewById<TextView>(R.id.txtAttendance)
        val btnPresent = findViewById<Button>(R.id.btnPresent)
        val btnAbsent = findViewById<Button>(R.id.btnAbsent)

        btnPresent.setOnClickListener {
            present++
            total++
            txtAttendance.text = "Attendance: $present / $total"
        }

        btnAbsent.setOnClickListener {
            total++
            txtAttendance.text = "Attendance: $present / $total"
        }
    }
}