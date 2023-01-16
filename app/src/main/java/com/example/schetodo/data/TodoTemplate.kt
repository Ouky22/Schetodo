package com.example.schetodo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TodoTemplate(
    @PrimaryKey val templateId: Int,
    val name: String
)
