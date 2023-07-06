package com.example.schetodo.data.todo_block

import androidx.room.*
import com.example.schetodo.data.schedule_template.ScheduleTemplate
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    indices = [
        Index("date"),
        Index("templateId")
    ],
    foreignKeys = [
        ForeignKey(
            entity = ScheduleTemplate::class,
            parentColumns = ["templateId"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class TodoBlock(
    @PrimaryKey(autoGenerate = true) val todoBlockId: Int,
    val notes: String?,
    val date: LocalDate?,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val templateId: Int?,
    @ColumnInfo(name = "markedForDeletion", defaultValue = "0")
    val markedForDeletion: Boolean = false
)