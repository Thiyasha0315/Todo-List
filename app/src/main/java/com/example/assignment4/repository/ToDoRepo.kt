package com.example.assignment4.repository

import androidx.lifecycle.LiveData
import com.example.assignment4.dao.ToDoDao
import com.example.assignment4.entities.Tasks

class ToDoRepo(private val taskDao: ToDoDao) {
    val allTasks: LiveData<List<Tasks>> = taskDao.getAllTasks()

    suspend fun insert(tasks: Tasks) {
        taskDao.insert(tasks)
    }

    suspend fun delete(tasks: Tasks) {
        taskDao.delete(tasks)
    }

    suspend fun update(tasks: Tasks) {
        taskDao.update(tasks)
    }
}