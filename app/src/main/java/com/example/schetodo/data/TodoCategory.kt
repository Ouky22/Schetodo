package com.example.schetodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoCategory(
    @PrimaryKey(autoGenerate = true) val categoryId: Int,
    val name: String,
    val color: Int,
    val parentTodoCategoryId: Int? // TODO add foreign key constraint
)