package com.example.schetodo.data.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.schetodo.data.TODO_BLOCK_CATEGORY_RELATIONSHIP_TABLE_NAME
import com.example.schetodo.data.todo_block.TodoBlock
import com.example.schetodo.data.todo_category.TodoCategory

@Entity(
    primaryKeys = ["todoBlockId", "categoryId"],
    foreignKeys = [
        ForeignKey(
            entity = TodoBlock::class,
            parentColumns = ["todoBlockId"],
            childColumns = ["todoBlockId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TodoCategory::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")],
    tableName = TODO_BLOCK_CATEGORY_RELATIONSHIP_TABLE_NAME
)
data class TodoBlockCategoryRelationship(
    val todoBlockId: Int,
    val categoryId: Int
)