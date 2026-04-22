package com.example.managemate

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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

        attendanceMap = loadData()

        // Rebuild UI
        for (subject in attendanceMap.keys) {
            container.addView(createSubjectView(subject))
        }

        btnAddSubject.setOnClickListener {
            val subject = edtSubject.text.toString().trim()

            if (subject.isNotEmpty()) {
                if (!attendanceMap.containsKey(subject)) {
                    attendanceMap[subject] = Attendance()
                    saveData()
                    container.addView(createSubjectView(subject))
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

        updateUI(txtResult, subject)

        // Add attendance
        btnPresent.setOnClickListener {
            val data = attendanceMap[subject]!!
            data.present++
            saveData()
            updateUI(txtResult, subject)
        }

        btnAbsent.setOnClickListener {
            val data = attendanceMap[subject]!!
            data.absent++
            saveData()
            updateUI(txtResult, subject)
        }

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

        // LONG PRESS MENU
        title.setOnLongClickListener {

            val options = arrayOf(
                "Edit Subject",
                "Delete Subject",
                "Reset Attendance",
                "Undo Last Change"
            )

            android.app.AlertDialog.Builder(this)
                .setTitle(subject)
                .setItems(options) { _, which ->

                    when (which) {

                        // ✏️ EDIT
                        0 -> {
                            val input = EditText(this)
                            input.setText(subject)

                            android.app.AlertDialog.Builder(this)
                                .setTitle("Edit Subject")
                                .setView(input)
                                .setPositiveButton("Save") { _, _ ->

                                    val newName = input.text.toString().trim()

                                    if (newName.isNotEmpty() && !attendanceMap.containsKey(newName)) {
                                        val data = attendanceMap[subject]!!
                                        attendanceMap.remove(subject)
                                        attendanceMap[newName] = data
                                        saveData()
                                        recreate()
                                    }
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }

                        // 🗑 DELETE (Direct options, no extra title)
                        1 -> {
                            val deleteOptions = arrayOf(
                                "Delete Only Attendance",
                                "Delete Entire Subject"
                            )

                            android.app.AlertDialog.Builder(this)
                                .setItems(deleteOptions) { _, choice ->

                                    when (choice) {

                                        0 -> {
                                            attendanceMap[subject] = Attendance()
                                            saveData()
                                            updateUI(txtResult, subject)
                                        }

                                        1 -> {
                                            attendanceMap.remove(subject)
                                            saveData()
                                            recreate()
                                        }
                                    }
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        }

                        // 🔄 RESET
                        2 -> {
                            attendanceMap[subject] = Attendance()
                            saveData()
                            updateUI(txtResult, subject)
                        }

                        // ↩️ UNDO
                        3 -> {
                            val data = attendanceMap[subject]!!
                            if (data.present > 0) data.present--
                            else if (data.absent > 0) data.absent--
                            else if (data.holiday > 0) data.holiday--

                            saveData()
                            updateUI(txtResult, subject)
                        }
                    }
                }
                .show()

            true
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
        } else 0

        txtResult.text =
            "Present: ${data.present} | Absent: ${data.absent} | Holiday: ${data.holiday}\n" +
                    "Total Classes: $totalClasses | Attendance: $percentage%"
    }

    private fun saveData() {
        val prefs = getSharedPreferences("attendance_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        val json = Gson().toJson(attendanceMap)
        editor.putString("attendance_data", json)
        editor.apply()
    }

    private fun loadData(): HashMap<String, Attendance> {
        val prefs = getSharedPreferences("attendance_prefs", MODE_PRIVATE)
        val json = prefs.getString("attendance_data", null)

        val type = object : TypeToken<HashMap<String, Attendance>>() {}.type

        return if (json != null) Gson().fromJson(json, type) else HashMap()
    }
}