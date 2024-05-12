package com.example.assignment4.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class ToDo(
    var item: String?,
    var description: String?,
    var priority: String?,
    var deadline: String?


) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}