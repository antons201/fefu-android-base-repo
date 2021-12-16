package ru.fefu.activitytracker.activitytracking.cards.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object TimeUtils {
    fun getDuration(start_time : LocalDateTime, stop_time : LocalDateTime) : String {
        val duration = Duration.between(stop_time, start_time).abs()
        val minutes = duration.toMinutes().toInt()

        return "${minutes/60} ч. ${minutes%60} мин."
    }

    fun getSpentTime(stop_time : LocalDateTime) : String {
        val current_time = LocalDateTime.now()

        return "${Duration.between(stop_time, current_time).abs().toHours().toInt()} ч. назад"
    }

    fun getTimeByStr(time: LocalDateTime) : String {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        return time.format(formatter)
    }
 }