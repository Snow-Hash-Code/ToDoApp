package com.example.lista_de_tareas_app.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build.VERSION
import com.example.lista_de_tareas_app.model.TodoModel

class DatabaseHandler(context: Context) : SQLiteOpenHelper(context, NAME, null, VERSION) {
    private lateinit var db: SQLiteDatabase

    companion object{
        private const val VERSION  = 1
        private const val NAME = "listaTareasApp"
        private const val TODO_TABLE = "ToDo"
        private const val ID = "id"
        private const val TASK = "task"
        private const val STATUS = "status"
        private const val CREATE_TODO_TABLE ="CREATE TABLE $TODO_TABLE ($ID INTEGER PRIMARY KEY AUTOINCREMENT, $TASK, $STATUS INTEGER)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        if (db != null) {
            db.execSQL(CREATE_TODO_TABLE)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //Drop the older tables
        if (db != null) {
            db.execSQL("DROP TABLE IF EXISTS $TODO_TABLE")
        }
        onCreate(db)
    }

    fun OpenDatabase(){
        db = this.writableDatabase
    }

    fun insertTask(task:TodoModel){
        val cv = ContentValues()
        cv.put(TASK,task.task)
        cv.put(STATUS,0)
        db.insert(TODO_TABLE,null,cv)
    }

    @SuppressLint("Range")
    fun getAllTasks(): List<TodoModel> {
        val taskList = ArrayList<TodoModel>()
        db.beginTransaction()
        try {
            db.query(TODO_TABLE, null, null, null, null, null, null).use { cur ->
                if (cur.moveToFirst()) {
                    do {
                        val task = TodoModel().apply {
                            id = cur.getInt(cur.getColumnIndex(ID))
                            task = cur.getString(cur.getColumnIndex(TASK))
                            status = cur.getInt(cur.getColumnIndex(STATUS))
                        }
                        taskList.add(task)
                    } while (cur.moveToNext())
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        return taskList
    }
    fun updateStatus(id: Int, status: Int) {
        val cv = ContentValues().apply {
            put(STATUS, status)
        }
        db.update(TODO_TABLE, cv, "$ID= ?", arrayOf(id.toString()))
    }

    fun updateTask(id: Int, task: String) {
        val cv = ContentValues().apply {
            put(TASK, task)
        }
        db.update(TODO_TABLE, cv, "$ID= ?", arrayOf(id.toString()))
    }

    fun deleteTask(id: Int) {
        db.delete(TODO_TABLE, "$ID= ?", arrayOf(id.toString()))
    }

}