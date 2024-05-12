package com.example.assignment4.database.repository

import com.example.assignment4.database.ToDoDatabase
import com.example.assignment4.database.entities.ToDo

class ToDoRepo(private val db:ToDoDatabase) {
    suspend fun insert(todo: ToDo) = db.getTodoDao().insertTodo(todo)
    suspend fun delete(todo:ToDo) = db.getTodoDao().deleteTodo(todo)
    fun getAllTodoItems():List<ToDo> = db.getTodoDao().getAllTodoItems()

    suspend fun update(todo:ToDo) = db.getTodoDao().updateTodo(todo)
}