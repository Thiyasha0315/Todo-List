package com.example.assignment4

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.assignment4.entities.Tasks
import com.example.assignment4.repository.ToDoRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ToDoViewModel(private val repository: ToDoRepo) : ViewModel() {
    val allTasks: LiveData<List<Tasks>> = repository.allTasks

    fun insert(tasks: Tasks) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(tasks)
    }

    fun delete(tasks: Tasks) = viewModelScope.launch(Dispatchers.IO) {
        repository.delete(tasks)
    }

    fun update(tasks: Tasks) = viewModelScope.launch(Dispatchers.IO) {
        repository.update(tasks)
    }
}