package com.example.assignment4

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.assignment4.database.entities.ToDo

class ToDoViewModel: ViewModel() {
    private val _data = MutableLiveData<List<ToDo>>()
    val data:LiveData<List<ToDo>> = _data
    fun setData(data:List<ToDo>){
        _data.value = data
    }
}