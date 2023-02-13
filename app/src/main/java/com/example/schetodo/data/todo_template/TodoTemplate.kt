package com.example.schetodo.data.todo_template

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoTemplate(
    @PrimaryKey val templateId: Int,
    val name: String
)
