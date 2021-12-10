package ru.fefu.activitytracker.activitytracking.cards.utils

import ru.fefu.activitytracker.activitytracking.train.TrainForegroundService

object DistanceUtils {
    fun getDistanceByString(distance: Double) : String {
        return if (distance > 1000) {
            "%.1f км.".format(distance/1000)
        } else {
            "%.0f м.".format(distance)
        }
    }
}