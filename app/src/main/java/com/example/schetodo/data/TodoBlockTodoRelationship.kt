package com.example.schetodo.data

import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    primaryKeys = ["todoBlockId", "todoId"],
    foreignKeys = [
        ForeignKey(
            entity = TodoBlock::class,
            parentColumns = ["todoBlockId"],
            childColumns = ["todoBlockId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Todo::class,
            parentColumns = ["todoId"],
            childColumns = ["todoId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class TodoBlockTodoRelationship(
    val todoBlockId: Int,
    val todoId: Int
)