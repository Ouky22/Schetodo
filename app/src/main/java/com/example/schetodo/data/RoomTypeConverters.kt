package com.example.schetodo.data

import androidx.room.TypeConverter
import com.example.schetodo.data.entity.TodoPriority
import java.time.LocalDate
import java.time.LocalTime

class RoomTypeConverters {
    @TypeConverter
    fun dateStampToDate(dateStampInDays: Long): LocalDate {
        return LocalDate.ofEpochDay(dateStampInDays)
    }

    @TypeConverter
    fun dateToDateStamp(date: LocalDate): Long {
        return date.toEpochDay()
    }

    @TypeConverter
    fun timeStampToTime(timeStampInSeconds: Long): LocalTime {
        return LocalTime.ofSecondOfDay(timeStampInSeconds)
    }

    @TypeConverter
    fun timeToTimeStamp(time: LocalTime): Int {
        return time.toSecondOfDay()
    }

    @TypeConverter
    fun todoPriorityToNumber(todoPriority: TodoPriority): Int {
        return todoPriority.priorityNumber
    }

    @TypeConverter
    fun numberToTodoPriority(number: Int): TodoPriority {
        return TodoPriority.getByPriorityNumber(number)
    }
}