package com.example.schetodo.data.todo

import androidx.room.*
import com.example.schetodo.data.todo_category.TodoCategory

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TodoCategory::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("categoryId")
    ]
)
data class Todo(
    @PrimaryKey(autoGenerate = true) val todoId: Int,
    val description: String,
    val priority: TodoPriority,
    val flag: TodoFlag,
    val categoryId: Int,
    @ColumnInfo(name = "markedForDeletion", defaultValue = "0")
    val markedForDeletion: Boolean = false
)

enum class TodoFlag {
    UNDONE, IN_PROGRESS, DONE, RECURRING
}

enum class TodoPriority(val priorityNumber: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    VERY_HIGH(4);

    companion object {
        fun getByPriorityNumber(priorityNumber: Int): TodoPriority =
            values().find { it.priorityNumber == priorityNumber }
                ?: throw NoSuchElementException("There is no TodoPriority with priorityNumber $priorityNumber")
    }
}