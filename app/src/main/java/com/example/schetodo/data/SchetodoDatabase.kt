package com.example.schetodo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.schetodo.data.todo_block.TodoBlockDao
import com.example.schetodo.data.todo_category.TodoCategoryDao
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationship
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.TodoBlockTodoRelationship
import com.example.schetodo.data.relationship.TodoBlockTodoRelationshipDao
import com.example.schetodo.data.schedule_block.ScheduleBlockDao
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.todo_template.TodoTemplate

@Database(
    entities = [
        Todo::class, TodoBlock::class, TodoCategory::class, TodoTemplate::class, Notification::class,
        TodoBlockCategoryRelationship::class, TodoBlockTodoRelationship::class
    ],
    version = 2,
    exportSchema = false
)
@androidx.room.TypeConverters(RoomTypeConverters::class)
abstract class SchetodoDatabase : RoomDatabase() {

    abstract val todoCategoryDao: TodoCategoryDao
    abstract val todoDao: TodoDao
    abstract val todoBlockDao: TodoBlockDao
    abstract val todoBlockCategoryRelationshipDao: TodoBlockCategoryRelationshipDao
    abstract val todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao
    abstract val scheduleBlockDao: ScheduleBlockDao

    companion object {
        @Volatile
        private var INSTANCE: SchetodoDatabase? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context,
                SchetodoDatabase::class.java,
                "schetodo_database"
            )
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }
}