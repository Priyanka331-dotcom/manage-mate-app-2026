package com.example.managemate

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class TodoActivity : AppCompatActivity() {

    private val tasks = mutableListOf<Task>()
    private lateinit var adapter: BaseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_todo)

        val edtTask = findViewById<EditText>(R.id.edtTask)
        val btnAddTask = findViewById<Button>(R.id.btnAddTask)
        val listTasks = findViewById<ListView>(R.id.listTasks)

        // ---------------- CUSTOM ADAPTER ----------------
        adapter = object : BaseAdapter() {

            override fun getCount(): Int = tasks.size
            override fun getItem(position: Int): Any = tasks[position]
            override fun getItemId(position: Int): Long = position.toLong()

            override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

                val view = LayoutInflater.from(this@TodoActivity)
                    .inflate(R.layout.task_item, parent, false)

                val txtTask = view.findViewById<TextView>(R.id.txtTask)
                val btnMenu = view.findViewById<ImageButton>(R.id.btnMenu)

                val task = tasks[position]

                txtTask.text = buildTaskText(task)

                // ---------------- ARROW BUTTON = ADD SUBTASK ----------------
                btnMenu.setOnClickListener {

                    val input = EditText(this@TodoActivity)

                    AlertDialog.Builder(this@TodoActivity)
                        .setTitle("Add Subtask")
                        .setView(input)
                        .setPositiveButton("Add") { _, _ ->
                            val sub = input.text.toString()

                            if (sub.isNotEmpty()) {
                                task.subtasks.add(sub)
                                notifyDataSetChanged()
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }

                // ---------------- LONG PRESS MENU ----------------
                view.setOnLongClickListener {

                    val options = arrayOf(
                        "Edit Task",
                        "Delete Task",
                        "Edit Subtask",
                        "Delete Subtask"
                    )

                    AlertDialog.Builder(this@TodoActivity)
                        .setTitle("Choose Action")
                        .setItems(options) { _, which ->

                            when (which) {

                                // ---------------- EDIT TASK ----------------
                                0 -> {
                                    val input = EditText(this@TodoActivity)
                                    input.setText(task.title)

                                    AlertDialog.Builder(this@TodoActivity)
                                        .setTitle("Edit Task")
                                        .setView(input)
                                        .setPositiveButton("Save") { _, _ ->
                                            task.title = input.text.toString()
                                            notifyDataSetChanged()
                                        }
                                        .setNegativeButton("Cancel", null)
                                        .show()
                                }

                                // ---------------- DELETE TASK ----------------
                                1 -> {
                                    AlertDialog.Builder(this@TodoActivity)
                                        .setTitle("Delete Task")
                                        .setMessage("Are you sure?")
                                        .setPositiveButton("Yes") { _, _ ->
                                            tasks.removeAt(position)
                                            notifyDataSetChanged()
                                        }
                                        .setNegativeButton("No", null)
                                        .show()
                                }

                                // ---------------- EDIT SUBTASK ----------------
                                2 -> {
                                    val subList = task.subtasks

                                    if (subList.isEmpty()) {
                                        Toast.makeText(
                                            this@TodoActivity,
                                            "No subtasks",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@setItems
                                    }

                                    AlertDialog.Builder(this@TodoActivity)
                                        .setTitle("Select Subtask to Edit")
                                        .setItems(subList.toTypedArray()) { _, subIndex ->

                                            val input = EditText(this@TodoActivity)
                                            input.setText(subList[subIndex])

                                            AlertDialog.Builder(this@TodoActivity)
                                                .setTitle("Edit Subtask")
                                                .setView(input)
                                                .setPositiveButton("Save") { _, _ ->
                                                    subList[subIndex] = input.text.toString()
                                                    notifyDataSetChanged()
                                                }
                                                .setNegativeButton("Cancel", null)
                                                .show()
                                        }
                                        .show()
                                }

                                // ---------------- DELETE SUBTASK ----------------
                                3 -> {
                                    val subList = task.subtasks

                                    if (subList.isEmpty()) {
                                        Toast.makeText(
                                            this@TodoActivity,
                                            "No subtasks",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        return@setItems
                                    }

                                    AlertDialog.Builder(this@TodoActivity)
                                        .setTitle("Select Subtask")
                                        .setItems(subList.toTypedArray()) { _, subIndex ->

                                            AlertDialog.Builder(this@TodoActivity)
                                                .setTitle("Delete Subtask")
                                                .setMessage("Are you sure?")
                                                .setPositiveButton("Yes") { _, _ ->
                                                    subList.removeAt(subIndex)
                                                    notifyDataSetChanged()
                                                }
                                                .setNegativeButton("No", null)
                                                .show()
                                        }
                                        .show()
                                }
                            }
                        }
                        .show()

                    true
                }

                return view
            }
        }

        listTasks.adapter = adapter

        // ---------------- ADD TASK ----------------
        btnAddTask.setOnClickListener {
            val text = edtTask.text.toString()

            if (text.isNotEmpty()) {
                tasks.add(Task(text))
                adapter.notifyDataSetChanged()
                edtTask.text.clear()
            }
        }
    }

    // ---------------- FORMAT TASK TEXT ----------------
    private fun buildTaskText(task: Task): String {
        return if (task.subtasks.isEmpty()) {
            task.title
        } else {
            task.title + "\n  ↳ " + task.subtasks.joinToString("\n  ↳ ")
        }
    }
}