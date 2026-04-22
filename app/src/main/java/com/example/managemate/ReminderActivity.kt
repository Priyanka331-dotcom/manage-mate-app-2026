package com.example.managemate

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class ReminderActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_reminder)

        val btnNotify = findViewById<Button>(R.id.btnNotify)

        btnNotify.setOnClickListener {
            NotificationHelper.sendNotification(this)
        }
    }
}