package com.example.lab4

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter(private var task: List<Task>, context: Context) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val db: TaskDatabaseHelper = TaskDatabaseHelper(context)
    private var sortedTasks: MutableList<Task> = ArrayList()

    init {
        sortedTasks.addAll(task)
    }

    class TaskViewHolder(itemview: View) : RecyclerView.ViewHolder(itemview){
        val titleTextView: TextView = itemview.findViewById(R.id.titleTextView)
        val priorityTextView: TextView = itemview.findViewById(R.id.priorityTextView)
        val deadlineTextView: TextView = itemview.findViewById(R.id.deadlineTextView)
        val contentTextView: TextView = itemview.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemview.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemview.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = task.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = task[position]
        holder.titleTextView.text = task.title
        holder.priorityTextView.text = task.priority
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val deadlineString = dateFormat.format(task.deadline)
        holder.deadlineTextView.text = deadlineString
        holder.contentTextView.text = task.content

        holder.updateButton.setOnClickListener{
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener{
            db.deleteTask(task.id)
            refreshData(db.getAllTask())
            Toast.makeText(holder.itemView.context, "Task Deleted", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData(newTask: List<Task>){
        task = newTask
        sortedTasks.clear()
        sortedTasks.addAll(newTask)
        notifyDataSetChanged()
    }

    fun sortByPriority() {
        sortedTasks = task.toMutableList()
        sortedTasks.sortBy { it.priority }
        notifyDataSetChanged()
    }

    fun sortByDeadline() {
        sortedTasks = task.toMutableList()
        sortedTasks.sortBy { it.deadline }
        notifyDataSetChanged()
    }
}