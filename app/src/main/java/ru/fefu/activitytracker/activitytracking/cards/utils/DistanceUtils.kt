package ru.fefu.activitytracker.activitytracking.cards.utils

import android.location.Location
import ru.fefu.activitytracker.activitytracking.train.TrainForegroundService

object DistanceUtils {
    fun getDistanceByString(distance: Double) : String {
        return if (distance > 1000) {
            "%.1f км.".format(distance/1000)
        } else {
            "%.0f м.".format(distance)
        }
    }

    fun getDistanceUsingCoordinatesList(coordinates: List<Pair<Double, Double>>) : Double {
        var distance = 0.0
        if (coordinates.size > 1) {
            for (i in 1 until coordinates.size) {
                val currentLocation = Location("CurrentLocation")
                currentLocation.latitude = coordinates[i].first
                currentLocation.longitude = coordinates[i].second

                val prevLocation = Location("PrevLocation")
                prevLocation.latitude = coordinates[i-1].first
                prevLocation.longitude = coordinates[i-1].second

                distance += currentLocation.distanceTo(prevLocation)
            }
        }

        return distance
    }

    fun getDistanceUsingCoordinates(prevCoordinate: Pair<Double, Double>,
                                    currCoordinate: Pair<Double, Double>) : Double {

        val currentLocation = Location("CurrentLocation")
        currentLocation.latitude = currCoordinate.first
        currentLocation.longitude = currCoordinate.second

        val prevLocation = Location("PrevLocation")
        prevLocation.latitude = prevCoordinate.first
        prevLocation.longitude = prevCoordinate.second

        return currentLocation.distanceTo(prevLocation).toDouble()
    }
}