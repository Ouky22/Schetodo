package com.example.schetodo.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val description: String?,
    val priority: TodoPriority,
    val flag: TodoFlag,
    val categoryId: Int
)

enum class TodoFlag {
    UNDONE, IN_PROGRESS, DONE, RECURRING
}

enum class TodoPriority {
    LOW, MEDIUM, HIGH, VERY_HIGH
}