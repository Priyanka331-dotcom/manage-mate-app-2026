package com.example.managemate

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Data class
data class Attendance(
    var present: Int = 0,
    var absent: Int = 0,
    var holiday: Int = 0
)

class AttendanceActivity : AppCompatActivity() {

    private lateinit var attendanceMap: HashMap<String, Attendance>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_attendance)

        val edtSubject = findViewById<EditText>(R.id.edtSubject)
        val btnAddSubject = findViewById<Button>(R.id.btnAddSubject)
        val container = findViewById<LinearLayout>(R.id.subjectContainer)

        // LOAD DATA
        attendanceMap = loadData()

        // REBUILD UI from saved data
        for (subject in attendanceMap.keys) {
            val subjectView = createSubjectView(subject)
            container.addView(subjectView)
        }

        // Add subject
        btnAddSubject.setOnClickListener {
            val subject = edtSubject.text.toString().trim()

            if (subject.isNotEmpty()) {

                if (!attendanceMap.containsKey(subject)) {
                    attendanceMap[subject] = Attendance()
                    saveData()

                    val subjectView = createSubjectView(subject)
                    container.addView(subjectView)
                }

                edtSubject.text.clear()
            } else {
                Toast.makeText(this, "Enter subject", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createSubjectView(subject: String): LinearLayout {

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(0, 20, 0, 20)

        val title = TextView(this)
        title.text = "$subject ▼"
        title.textSize = 18f

        val innerLayout = LinearLayout(this)
        innerLayout.orientation = LinearLayout.VERTICAL
        innerLayout.visibility = View.GONE

        val btnPresent = Button(this)
        btnPresent.text = "Present"

        val btnAbsent = Button(this)
        btnAbsent.text = "Absent"

        val btnHoliday = Button(this)
        btnHoliday.text = "Holiday"

        val txtResult = TextView(this)

        // Show correct data initially
        updateUI(txtResult, subject)

        // Present
        btnPresent.setOnClickListener {
            val data = attendanceMap[subject]!!
            data.present++
            saveData()
            updateUI(txtResult, subject)
        }

        // Absent
        btnAbsent.setOnClickListener {
            val data = attendanceMap[subject]!!
            data.absent++
            saveData()
            updateUI(txtResult, subject)
        }

        // Holiday
        btnHoliday.setOnClickListener {
            val data = attendanceMap[subject]!!
            data.holiday++
            saveData()
            updateUI(txtResult, subject)
        }

        // Expand / Collapse
        title.setOnClickListener {
            innerLayout.visibility =
                if (innerLayout.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        innerLayout.addView(btnPresent)
        innerLayout.addView(btnAbsent)
        innerLayout.addView(btnHoliday)
        innerLayout.addView(txtResult)

        layout.addView(title)
        layout.addView(innerLayout)

        return layout
    }

    private fun updateUI(txtResult: TextView, subject: String) {
        val data = attendanceMap[subject]!!

        val totalClasses = data.present + data.absent
        val percentage = if (totalClasses > 0) {
            (data.present * 100) / totalClasses
        } else {
            0
        }

        txtResult.text =
            "Present: ${data.present} | Absent: ${data.absent} | Holiday: ${data.holiday}\n" +
                    "Total Classes: $totalClasses | Attendance: $percentage%"
    }

    // SAVE DATA
    private fun saveData() {
        val prefs = getSharedPreferences("attendance_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        val gson = Gson()
        val json = gson.toJson(attendanceMap)

        editor.putString("attendance_data", json)
        editor.apply()
    }

    // LOAD DATA
    private fun loadData(): HashMap<String, Attendance> {
        val prefs = getSharedPreferences("attendance_prefs", MODE_PRIVATE)
        val gson = Gson()

        val json = prefs.getString("attendance_data", null)

        val type = object : TypeToken<HashMap<String, Attendance>>() {}.type

        return if (json != null) {
            gson.fromJson(json, type)
        } else {
            HashMap()
        }
    }
}