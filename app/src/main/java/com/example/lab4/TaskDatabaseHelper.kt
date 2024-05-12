package com.example.lab4

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.content.contentValuesOf
import java.text.SimpleDateFormat
import java.util.Locale

class TaskDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "Task.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "task"
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_PRIORITY = "priority"
        private const val COLUMN_DEADLINE = "deadline"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_TITLE TEXT, $COLUMN_CONTENT TEXT, $COLUMN_PRIORITY TEXT, $COLUMN_DEADLINE DATE)"
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertTask(task: Task){
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val deadlineString = dateFormat.format(task.deadline)

        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_CONTENT, task.content)
            put(COLUMN_PRIORITY, task.priority)
            put(COLUMN_DEADLINE, deadlineString)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun getAllTask(sortBy: String = ""): List<Task> {
        val taskList = mutableListOf<Task>()
        val db = readableDatabase
        var cursor: Cursor? = null
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        try {
            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME ${
                if (sortBy.isNotBlank()) "ORDER BY " +
                        if (sortBy == "priority") "$COLUMN_PRIORITY = 'High' DESC, $COLUMN_PRIORITY = 'Medium' DESC, $COLUMN_PRIORITY = 'Low' DESC, $COLUMN_DEADLINE" else "$COLUMN_DEADLINE" else ""
            }", null)

            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
                val deadlineString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
                val deadline = dateFormat.parse(deadlineString)

                val task = Task(id, title, content, priority, deadline)
                taskList.add(task)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return taskList
    }



    fun updateTask(task: Task){
        val db = writableDatabase
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val deadlineString = dateFormat.format(task.deadline)
        val values = ContentValues().apply {
            put(COLUMN_TITLE, task.title)
            put(COLUMN_CONTENT, task.content)
            put(COLUMN_PRIORITY, task.priority)
            put(COLUMN_DEADLINE, deadlineString)
        }
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(task.id.toString())
        db.update(TABLE_NAME, values, whereClause, whereArgs)
        db.close()
    }

    fun getTaskById(taskId: Int): Task{
        val db = readableDatabase
        val query = "SELECT*FROM $TABLE_NAME WHERE $COLUMN_ID = $taskId"
        val cursor = db.rawQuery(query, null)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        cursor.moveToFirst()

        val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
        val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
        val deadlineString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
        val deadline = dateFormat.parse(deadlineString)

        cursor.close()
        db.close()
        return Task(id, title, content, priority, deadline)
    }

    fun deleteTask(taskId: Int){
        val db = writableDatabase
        val whereClause = "$COLUMN_ID = ?"
        val whereArgs = arrayOf(taskId.toString())

        db.delete(TABLE_NAME, whereClause, whereArgs)
        db.close()
    }

    fun getTaskWithShortestDeadline(): Task? {
        val db = readableDatabase
        var cursor: Cursor? = null
        var task: Task? = null

        try {
            val currentDate = System.currentTimeMillis()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val currentDateString = dateFormat.format(currentDate)
            cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE $COLUMN_DEADLINE >= '$currentDateString' ORDER BY $COLUMN_DEADLINE ASC LIMIT 1", null)
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE))
                val content = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTENT))
                val priority = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITY))
                val deadlineString = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE))
                val deadline = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(deadlineString)

                task = Task(id, title, content, priority, deadline)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
            db.close()
        }
        return task
    }

}