package com.example.assignment4.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.assignment4.database.entities.ToDo

@Dao
interface TodoDao {
    @Insert
    suspend fun insertTodo(
        todo: ToDo
    )

    @Delete
    suspend fun deleteTodo(
        todo: ToDo
    )

    @Query("SELECT * FROM Todo")
    fun getAllTodoItems(): List<ToDo>


    @Update
    suspend fun updateTodo(todo: ToDo)
}