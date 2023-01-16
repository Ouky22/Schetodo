package com.example.schetodo.data

import androidx.room.TypeConverter
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
}