package com.example.assignment4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.assignment4.database.dao.TodoDao
import com.example.assignment4.database.entities.ToDo


val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Migration code to update schema from version 1 to version 2
        db.execSQL("ALTER TABLE ToDo ADD COLUMN description TEXT")
        db.execSQL("ALTER TABLE ToDo ADD COLUMN priority TEXT")
        db.execSQL("ALTER TABLE ToDo ADD COLUMN deadline TEXT")
    }
}
@Database(entities = [ToDo::class], version = 2)
abstract class ToDoDatabase:RoomDatabase(){
    abstract fun getTodoDao(): TodoDao
    companion object{
        @Volatile
        private var INSTANCE: ToDoDatabase? = null
        fun getInstance(context:Context):ToDoDatabase{
            synchronized(this){
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ToDoDatabase::class.java,
                    "todo_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}