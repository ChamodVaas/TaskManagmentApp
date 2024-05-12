package com.example.lab4

import TaskDAO
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lab4.databinding.ActivityUpdateTaskBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class UpdateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateTaskBinding
    private lateinit var db: TaskDAO
    private var taskId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = TaskDAO(this)

        taskId = intent.getIntExtra("task_id", -1)
        if (taskId == -1){
            finish()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            val task = db.getTaskById(taskId)
            if (task != null) {
                binding.editTitle.setText(task.title)
                binding.editContent.setText(task.content)
                // Set priority
                val priorityOptions = arrayOf("High", "Medium", "Low")
                val priorityAdapter = ArrayAdapter(
                    this@UpdateTaskActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    priorityOptions
                )
                binding.editPriority.adapter = priorityAdapter
                val priorityPosition = priorityOptions.indexOf(task.priority)
                if (priorityPosition != -1) {
                    binding.editPriority.setSelection(priorityPosition)
                }
                // Convert deadline Date to Calendar
                val calendar = Calendar.getInstance()
                calendar.time = task.deadline
                // Set DatePicker date
                binding.editdeadlineDatePicker.updateDate(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
            }
        }
        binding.UpdateSaveButton.setOnClickListener{
            val newTitle = binding.editTitle.text.toString()
            val newContent = binding.editContent.text.toString()
            val newPriority = binding.editPriority.selectedItem.toString()
            val year = binding.editdeadlineDatePicker.year
            val month = binding.editdeadlineDatePicker.month
            val dayOfMonth = binding.editdeadlineDatePicker.dayOfMonth
            val newDedline = Calendar.getInstance()
            newDedline.set(year, month, dayOfMonth)
            val updadteTask = Task(taskId, newTitle, newContent, newPriority, newDedline.time)
            CoroutineScope(Dispatchers.Main).launch {
                db.updateTask(updadteTask)
                finish()
                Toast.makeText(this@UpdateTaskActivity, "Changes Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }
}