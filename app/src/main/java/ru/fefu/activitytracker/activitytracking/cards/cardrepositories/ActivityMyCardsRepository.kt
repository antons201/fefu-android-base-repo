package ru.fefu.activitytracker.activitytracking

import ru.fefu.activitytracker.activitytracking.train.TrainTypes
import java.time.LocalDateTime

class ActivityMyCardsRepository {

    private val cardsList = listOf(
        ActivityPeriod(
            "13 ноября 2021"
        )
    )

    fun getActivityMyCards(): List<Card> = cardsList
}