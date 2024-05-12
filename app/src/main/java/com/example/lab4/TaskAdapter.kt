package com.example.lab4

import TaskDAO
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(private var tasks: List<Task>, private val taskDao: TaskDAO) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private var sortedTasks: MutableList<Task> = ArrayList()

    init {
        sortedTasks.addAll(tasks)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val priorityTextView: TextView = itemView.findViewById(R.id.priorityTextView)
        val deadlineTextView: TextView = itemView.findViewById(R.id.deadlineTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val updateButton: ImageView = itemView.findViewById(R.id.updateButton)
        val deleteButton: ImageView = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.titleTextView.text = task.title
        holder.priorityTextView.text = task.priority
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val deadlineString = dateFormat.format(task.deadline)
        holder.deadlineTextView.text = deadlineString
        holder.contentTextView.text = task.content

        holder.updateButton.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateTaskActivity::class.java).apply {
                putExtra("task_id", task.id)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch{
                taskDao.deleteTask(task.id)
                refreshData(taskDao.getAllTask())
                Toast.makeText(holder.itemView.context, "Task Deleted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun refreshData(newTasks: List<Task>) {
        tasks = newTasks
        sortedTasks.clear()
        sortedTasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    fun sortByPriority() {
        sortedTasks = tasks.toMutableList()
        sortedTasks.sortBy { it.priority }
        notifyDataSetChanged()
    }

    fun sortByDeadline() {
        sortedTasks = tasks.toMutableList()
        sortedTasks.sortBy { it.deadline }
        notifyDataSetChanged()
    }
}
