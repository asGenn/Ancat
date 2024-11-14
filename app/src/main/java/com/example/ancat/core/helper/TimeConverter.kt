package com.example.ancat.core.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeConverter {

    fun convertTime(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return format.format(date)
    }
}