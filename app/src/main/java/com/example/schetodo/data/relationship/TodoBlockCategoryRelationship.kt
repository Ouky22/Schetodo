package com.example.schetodo.data.relationship

import androidx.room.Entity
import androidx.room.ForeignKey
import com.example.schetodo.data.TodoBlock
import com.example.schetodo.data.TodoCategory

@Entity(
    primaryKeys = ["todoBlockId", "todoCategoryId"],
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
    ]
)
data class TodoBlockCategoryRelationship(
    val todoBlockId: Int,
    val categoryId: Int
)