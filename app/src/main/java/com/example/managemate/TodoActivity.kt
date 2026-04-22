package com.example.managemate

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TodoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_todo)

        val edtTask = findViewById<EditText>(R.id.edtTask)
        val edtDeadline = findViewById<EditText>(R.id.edtDeadline)
        val edtSubtask = findViewById<EditText>(R.id.edtSubtask)
        val spinnerPriority = findViewById<Spinner>(R.id.spinnerPriority)
        val btnAddSubtask = findViewById<Button>(R.id.btnAddSubtask)
        val txtSubtasks = findViewById<TextView>(R.id.txtSubtasks)

        val priorities = arrayOf("Very Important", "Important", "Less Important")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorities)
        spinnerPriority.adapter = adapter

        val subtaskList = mutableListOf<String>()

        btnAddSubtask.setOnClickListener {
            val sub = edtSubtask.text.toString()
            if (sub.isNotEmpty()) {
                subtaskList.add(sub)
                txtSubtasks.text = subtaskList.joinToString("\n")
                edtSubtask.text.clear()
            }
        }
    }
}