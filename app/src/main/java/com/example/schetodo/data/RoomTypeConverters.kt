package com.example.schetodo.data

import androidx.room.TypeConverter
import com.example.schetodo.data.todo.TodoPriority
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

class RoomTypeConverters {
    @TypeConverter
    fun dateStampToDate(dateStampInDays: Long?): LocalDate? {
        return dateStampInDays?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToDateStamp(date: LocalDate?): Long? {
        return date?.toEpochDay()
    }

    @TypeConverter
    fun timeStampToTime(timeStampInSeconds: Int): LocalTime {
        return LocalTime.ofSecondOfDay(timeStampInSeconds.toLong())
    }

    @TypeConverter
    fun timeToTimeStamp(time: LocalTime): Int {
        return time.toSecondOfDay()
    }

    @TypeConverter
    fun dateTimeToStamp(dateTime: LocalDateTime): Long {
        return dateTime.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun stampToDateTime(dateTimeStamp: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(dateTimeStamp, 0, ZoneOffset.UTC)
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