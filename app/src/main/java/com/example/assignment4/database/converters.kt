package com.example.assignment4.database

import androidx.room.TypeConverter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class converters {
    @TypeConverter
    fun fromDate(date: Date?): String? {
        return date?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(it) }
    }

    @TypeConverter
    fun toDate(timestamp: String?): Date? {
        return timestamp?.let { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).parse(it) }
    }

}