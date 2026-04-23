package com.example.managemate

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class CalendarActivity : AppCompatActivity() {

    private lateinit var calendarView: CalendarView
    private lateinit var btnAddEvent: Button
    private lateinit var eventContainer: LinearLayout

    private var selectedDate: String = ""

    // ⭐ store all event dates
    private val eventDates = HashSet<String>()

    private val prefs by lazy {
        getSharedPreferences("calendar_events", Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        calendarView = findViewById(R.id.calendarView)
        btnAddEvent = findViewById(R.id.btnAddEvent)
        eventContainer = findViewById(R.id.eventContainer)

        selectedDate = getToday()

        loadEvents()

        // 📌 date selection
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            selectedDate = "$dayOfMonth/${month + 1}/$year"

            // ⭐ highlight effect if event exists
            if (eventDates.contains(selectedDate)) {
                Toast.makeText(
                    this,
                    "📌 Important event on this date!",
                    Toast.LENGTH_SHORT
                ).show()

                // visual feedback (simple trick)
                calendarView.setDate(calendarView.date, true, true)
            }

            loadEvents()
        }

        btnAddEvent.setOnClickListener {
            showAddEventDialog()
        }
    }

    // ➕ ADD EVENT
    private fun showAddEventDialog() {
        val view = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_event, null)

        val edtName = view.findViewById<EditText>(R.id.edtEventName)
        val timePicker = view.findViewById<TimePicker>(R.id.timePicker)

        timePicker.setIs24HourView(true)

        AlertDialog.Builder(this)
            .setTitle("Add Event ($selectedDate)")
            .setView(view)
            .setPositiveButton("Save") { _, _ ->

                val name = edtName.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Enter event name", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val time = String.format(
                    "%02d:%02d",
                    timePicker.hour,
                    timePicker.minute
                )

                saveEvent(name, selectedDate, time)
                loadEvents()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // 💾 SAVE EVENT
    private fun saveEvent(name: String, date: String, time: String) {

        val list = getEvents()

        val obj = JSONObject().apply {
            put("name", name)
            put("date", date)
            put("time", time)
        }

        list.put(obj)

        prefs.edit()
            .putString("events", list.toString())
            .apply()
    }

    // 📌 LOAD EVENTS + TRACK EVENT DATES
    private fun loadEvents() {

        eventContainer.removeAllViews()
        eventDates.clear()

        val list = getEvents()

        // collect event dates
        for (i in 0 until list.length()) {
            val obj = list.getJSONObject(i)
            eventDates.add(obj.getString("date"))
        }

        // show only selected date events
        for (i in 0 until list.length()) {

            val obj = list.getJSONObject(i)

            if (obj.getString("date") == selectedDate) {

                val card = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(30, 20, 30, 20)

                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        setMargins(0, 10, 0, 10)
                    }

                    setBackgroundColor(0xFFD8E2DC.toInt())
                }

                val text = TextView(this)
                text.text =
                    "📌 ${obj.getString("name")}\n📅 ${obj.getString("date")}\n⏰ ${obj.getString("time")}"

                card.addView(text)
                eventContainer.addView(card)
            }
        }
    }

    // 📦 SAFE LOADER
    private fun getEvents(): JSONArray {
        return try {
            val data = prefs.getString("events", "[]")
            JSONArray(data ?: "[]")
        } catch (e: Exception) {
            JSONArray()
        }
    }

    private fun getToday(): String {
        val cal = Calendar.getInstance()
        return "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
    }
}