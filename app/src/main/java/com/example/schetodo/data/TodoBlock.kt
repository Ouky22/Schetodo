package com.example.schetodo.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    indices = [
        Index("date")
    ]
)
data class TodoBlock(
    @PrimaryKey(autoGenerate = true) val todoBlockId: Int,
    val notes: String?,
    val date: LocalDate?,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val templateId: Int?
)