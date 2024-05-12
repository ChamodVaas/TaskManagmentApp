package com.example.lab4

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lab4.databinding.ActivityAddTaskBinding
import java.util.Calendar

class addTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding
    private lateinit var db: TaskDatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabaseHelper(this)

        val priorityOptions = arrayOf("High", "Medium", "Low")
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, priorityOptions)
        binding.addPriority.adapter = priorityAdapter

        binding.saveButton.setOnClickListener{
            val title = binding.addTitle.text.toString()
            val content = binding.addContent.text.toString()
            val priority = binding.addPriority.selectedItem.toString()
            val year = binding.deadlineDatePicker.year
            val month = binding.deadlineDatePicker.month
            val dayOfMonth = binding.deadlineDatePicker.dayOfMonth
            val deadline = Calendar.getInstance()
            deadline.set(year, month, dayOfMonth)
            val task = Task(0, title, content, priority, deadline.time)
            db.insertTask(task)
            finish()
            Toast.makeText(this, "Task Saved", Toast.LENGTH_SHORT).show()
        }
    }
}