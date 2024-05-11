package com.example.assignment4.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.assignment4.entities.Tasks

@Dao
interface ToDoDao {
    @Query("SELECT * FROM tasks ORDER BY id DESC")
    fun getAllTasks(): LiveData<List<Tasks>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tasks: Tasks)

    @Delete
    suspend fun delete(tasks: Tasks)

    @Update
    suspend fun update(tasks: Tasks)
}