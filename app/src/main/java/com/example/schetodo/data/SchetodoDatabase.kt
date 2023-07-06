package com.example.schetodo.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import com.example.schetodo.data.todo_block.TodoBlockDao
import com.example.schetodo.data.todo_category.TodoCategoryDao
import com.example.schetodo.data.todo.TodoDao
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.notification.NotificationDao
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationship
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationshipDao
import com.example.schetodo.data.relationship.TodoBlockTodoRelationship
import com.example.schetodo.data.relationship.TodoBlockTodoRelationshipDao
import com.example.schetodo.data.schedule_block.ScheduleBlockDao
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory
import com.example.schetodo.data.schedule_template.ScheduleTemplate

@Database(
    entities = [
        Todo::class, TodoBlock::class, TodoCategory::class, ScheduleTemplate::class, Notification::class,
        TodoBlockCategoryRelationship::class, TodoBlockTodoRelationship::class
    ],
    version = 7,
    autoMigrations = [
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6),
        AutoMigration(
            from = 6,
            to = 7,
            spec = SchetodoDatabase.RenameTodoTemplateToScheduleTemplate::class
        )
    ]
)
@TypeConverters(RoomTypeConverters::class)
abstract class SchetodoDatabase : RoomDatabase() {

    abstract val todoCategoryDao: TodoCategoryDao
    abstract val todoDao: TodoDao
    abstract val todoBlockDao: TodoBlockDao
    abstract val todoBlockCategoryRelationshipDao: TodoBlockCategoryRelationshipDao
    abstract val todoBlockTodoRelationshipDao: TodoBlockTodoRelationshipDao
    abstract val scheduleBlockDao: ScheduleBlockDao
    abstract val notificationDao: NotificationDao

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

    @RenameTable(
        fromTableName = "TodoTemplate",
        toTableName = "ScheduleTemplate"
    )
    class RenameTodoTemplateToScheduleTemplate : AutoMigrationSpec
}