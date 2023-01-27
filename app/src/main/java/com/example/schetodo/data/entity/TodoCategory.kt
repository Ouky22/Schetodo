package com.example.schetodo.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class TodoCategory(
    @PrimaryKey(autoGenerate = true) val categoryId: Int,
    val name: String,
    val color: Long,
    val parentTodoCategoryId: Int?
)