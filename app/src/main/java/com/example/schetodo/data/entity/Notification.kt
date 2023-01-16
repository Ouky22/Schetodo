package com.example.schetodo.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
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
    ]
)
data class Notification(
    val time: LocalTime,
    val todoBlockId: Int
)