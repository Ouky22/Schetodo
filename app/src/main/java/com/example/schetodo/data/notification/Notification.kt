package com.example.schetodo.data.notification

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.schetodo.data.todo_block.TodoBlock
import java.time.LocalTime

@Entity(
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
    @PrimaryKey(autoGenerate = true) val notificationId: Int,
    val time: LocalTime,
    val todoBlockId: Int
)