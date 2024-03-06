package com.example.lista_de_tareas_app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.example.lista_de_tareas_app.MainActivity
import com.example.lista_de_tareas_app.R
import com.example.lista_de_tareas_app.model.TodoModel
import com.example.lista_de_tareas_app.utils.DatabaseHandler
import com.example.lista_de_tareas_app.AddNewTask

class ToDoAdapter(private val db: DatabaseHandler, private val activity: MainActivity) :
    RecyclerView.Adapter<ToDoAdapter.ViewHolder>() {
    private var todoList: List<TodoModel> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.task_layout, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        db.OpenDatabase()

        val item = todoList[position]
        holder.task.text = item.task
        holder.task.isChecked = toBoolean(item.status)
        holder.task.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                db.updateStatus(item.id, 1)
            } else {
                db.updateStatus(item.id, 0)
            }
        }
    }

    private fun toBoolean(n: Int): Boolean {
        return n != 0
    }

    override fun getItemCount(): Int {
        return todoList.size
    }

    fun getContext(): Context {
        return activity
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(todoList: List<TodoModel>) {
        this.todoList = todoList
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        val item = todoList[position]
        db.deleteTask(item.id)
        todoList = todoList.toMutableList().apply {
            removeAt(position)
        }
        notifyItemRemoved(position)
    }

    fun editItem(position: Int) {
        val item = todoList[position]
        val bundle = Bundle()
        bundle.putInt("id", item.id)
        bundle.putString("task", item.task)
        val fragment = AddNewTask()
        fragment.arguments = bundle
        fragment.show(activity.supportFragmentManager, AddNewTask.TAG)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var task: CheckBox = view.findViewById(R.id.todoCheckBox) // Cambiado según especificación
    }
}