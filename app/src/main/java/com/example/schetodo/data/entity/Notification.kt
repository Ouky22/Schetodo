package com.example.schetodo.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.example.schetodo.data.todo_block.TodoBlock
import java.time.LocalTime

@Entity(
    primaryKeys = ["time", "todoBlockId"],
    foreignKeys = [
        ForeignKey(
            entity = TodoBlock::class,
            parentColumns = ["todoBlockId"],
            childColumns = ["todoBlockId"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("todoBlockId")]
)
data class Notification(
    val time: LocalTime,
    val todoBlockId: Int
)