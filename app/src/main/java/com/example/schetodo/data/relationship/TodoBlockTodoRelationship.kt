package com.example.schetodo.data.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.schetodo.data.TODO_BLOCK_TODO_RELATIONSHIP_TABLE_NAME
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_block.TodoBlock

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
    ],
    indices = [Index("todoId"), Index("todoBlockId")],
    tableName = TODO_BLOCK_TODO_RELATIONSHIP_TABLE_NAME
)
data class TodoBlockTodoRelationship(
    val todoBlockId: Int,
    val todoId: Int
)
