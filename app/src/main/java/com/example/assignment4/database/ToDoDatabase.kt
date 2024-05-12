package com.example.assignment4.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.assignment4.database.dao.TodoDao
import com.example.assignment4.database.entities.ToDo


val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create the new table with the correct schema
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `ToDo_new` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`item` TEXT, " +
                    "`description` TEXT, " +
                    "`priority` TEXT, " +
                    "`deadline` TEXT, " +
                    "`category` TEXT NOT NULL)"
        )

        // Copy data from the old table to the new one
        database.execSQL(
            "INSERT INTO `ToDo_new` " +
                    "(`id`, `item`, `description`, `priority`, `deadline`, `category`) " +
                    "SELECT `id`, `item`, `description`, `priority`, `deadline`, `category` FROM `ToDo`"
        )

        // Drop the old table
        database.execSQL("DROP TABLE IF EXISTS `ToDo`")

        // Rename the new table to match the original table name
        database.execSQL("ALTER TABLE `ToDo_new` RENAME TO `ToDo`")
    }
}
@Database(entities = [ToDo::class], version = 3)
@TypeConverters(converters::class) // Add this line to specify TypeConverter
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
                    .addMigrations(MIGRATION_2_3)
                    .build().also {
                    INSTANCE = it
                }
            }
        }
    }
}