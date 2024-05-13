import android.content.Context
import com.example.lab4.Task
import com.example.lab4.TaskDatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskViewModel(context: Context) {
    private val dbHelper: TaskDatabaseHelper = TaskDatabaseHelper(context)

    suspend fun insertTask(task: Task) {
        withContext(Dispatchers.IO) {
            dbHelper.insertTask(task)
        }
    }

    suspend fun getAllTask(sortBy: String = ""): List<Task> {
        return withContext(Dispatchers.IO) {
            dbHelper.getAllTask(sortBy)
        }
    }

    suspend fun updateTask(task: Task) {
        withContext(Dispatchers.IO) {
            dbHelper.updateTask(task)
        }
    }

    suspend fun getTaskById(taskId: Int): Task? {
        return withContext(Dispatchers.IO) {
            dbHelper.getTaskById(taskId)
        }
    }

    suspend fun deleteTask(taskId: Int) {
        withContext(Dispatchers.IO) {
            dbHelper.deleteTask(taskId)
        }
    }

    suspend fun getTaskWithShortestDeadline(): Task? {
        return withContext(Dispatchers.IO) {
            dbHelper.getTaskWithShortestDeadline()
        }
    }
}