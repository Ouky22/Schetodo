package com.example.schetodo.data.schedule_template

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ScheduleTemplate(
    @PrimaryKey val templateId: Int = 0,
    val name: String
)
