package com.example.lab4

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab4.databinding.ActivityMainBinding
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: TaskDatabaseHelper
    private lateinit var tasksAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDatabaseHelper(this)
        tasksAdapter = TaskAdapter(db.getAllTask(), this)

        binding.taskRecylcleView.layoutManager = LinearLayoutManager(this)
        binding.taskRecylcleView.adapter = tasksAdapter

        val sortOptions = arrayOf("Default", "Priority", "Deadline")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, sortOptions)
        binding.sortSpinner.adapter = adapter

        binding.addButton.setOnClickListener{
            val intent = Intent(this, addTaskActivity::class.java)
            startActivity(intent)
        }

        binding.sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                val selectedItem = sortOptions[position]
                if(selectedItem == "Priority"){
                    tasksAdapter.refreshData(db.getAllTask("priority"))
                } else if(selectedItem == "Deadline"){
                    tasksAdapter.refreshData(db.getAllTask("deadline"))
                }else{
                    tasksAdapter.refreshData(db.getAllTask())
                }
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

    override fun onResume() {
        super.onResume()
        tasksAdapter.refreshData(db.getAllTask())
        binding.sortSpinner.setSelection(0)

        // Displaying the shortest deadline task
        val taskWithShortestDeadline = db.getTaskWithShortestDeadline()
        if (taskWithShortestDeadline != null) {
            val currentDate = Calendar.getInstance().time
            val deadline = taskWithShortestDeadline.deadline
            val remainingTime = deadline.time - currentDate.time
            val remainingDays = remainingTime / (1000 * 60 * 60 * 24)
            val remainingHours = (remainingTime / (1000 * 60 * 60)) % 24
            val remainingMinutes = (remainingTime / (1000 * 60)) % 60

            val remainingFormatted: String

            if(remainingDays == 0L){
                remainingFormatted = String.format(
                    Locale.getDefault(),
                    "%02d Hours %02d Minutes",
                    remainingHours,
                    remainingMinutes
                )
            }else{
                remainingFormatted = String.format(
                    Locale.getDefault(),
                    "%02d Days %02d Hours %02d Minutes",
                    remainingDays,
                    remainingHours,
                    remainingMinutes
                )
            }

            val taskText = "$remainingFormatted remaining for ${taskWithShortestDeadline.title}"
            val words = taskText.split(" ") // Split the task text into words

            var formattedTaskText = ""
            var count = 0

            for (word in words) {
                formattedTaskText += "$word "
                count++
                if (count == 4) {
                    formattedTaskText += "\n"
                    count = 0
                }
            }

            binding.tasks.text = formattedTaskText.trim()
        } else {
            binding.tasks.text = "No tasks found"
        }

    }
}