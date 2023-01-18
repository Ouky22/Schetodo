package com.example.schetodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.schetodo.data.dao.TodoCategoryDao
import com.example.schetodo.data.entity.*
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationship
import com.example.schetodo.data.relationship.TodoBlockTodoRelationship

@Database(
    entities = [
        Todo::class, TodoBlock::class, TodoCategory::class, TodoTemplate::class, Notification::class,
        TodoBlockCategoryRelationship::class, TodoBlockTodoRelationship::class
    ],
    version = 1,
    exportSchema = false
)
@androidx.room.TypeConverters(RoomTypeConverters::class)
abstract class SchetodoDatabase : RoomDatabase() {

    abstract val todoCategoryDao: TodoCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: SchetodoDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context,
                SchetodoDatabase::class.java,
                "schetodo_database"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}