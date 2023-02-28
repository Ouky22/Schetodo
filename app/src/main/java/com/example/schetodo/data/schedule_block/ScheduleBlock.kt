package com.example.schetodo.data.schedule_block

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.schetodo.data.notification.Notification
import com.example.schetodo.data.relationship.TodoBlockCategoryRelationship
import com.example.schetodo.data.relationship.TodoBlockTodoRelationship
import com.example.schetodo.data.todo.Todo
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory

/**
 * Contains a TodoBlock with its associated Todos and TodoCategories
 */
data class ScheduleBlock(
    @Embedded val todoBlock: TodoBlock,

    @Relation(
        parentColumn = "todoBlockId",
        entityColumn = "todoId",
        associateBy = Junction(TodoBlockTodoRelationship::class)
    )
    val todos: List<Todo>,

    @Relation(
        parentColumn = "todoBlockId",
        entityColumn = "categoryId",
        associateBy = Junction(TodoBlockCategoryRelationship::class)
    )
    val todoCategories: List<TodoCategory>,

    @Relation(
        parentColumn = "todoBlockId",
        entityColumn = "todoBlockId"
    )
    val notifications: List<Notification> = emptyList()
)