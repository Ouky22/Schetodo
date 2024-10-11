package com.example.schetodo.data.todo_category

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.schetodo.data.TODO_CATEGORY_TABLE_NAME

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = TodoCategory::class,
            parentColumns = ["categoryId"],
            childColumns = ["parentTodoCategoryId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    tableName = TODO_CATEGORY_TABLE_NAME
)
data class TodoCategory(
    @PrimaryKey(autoGenerate = true) val categoryId: Int,
    val name: String,
    val color: Long,
    val parentTodoCategoryId: Int?,
    val iconName: String,
    @ColumnInfo(name = "markedForDeletion", defaultValue = "0")
    val markedForDeletion: Boolean = false
)
