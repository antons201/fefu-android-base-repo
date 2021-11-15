package ru.fefu.activitytracker.activitytracking

import ru.fefu.activitytracker.activitytracking.train.TrainTypes
import java.time.LocalDateTime

class ActivityUsersCardsRepository {

    private val cardsList = listOf(
        ActivityPeriod(
            "Вчера"
        ),
        ActivityUsersCard(
            "14.32 км",
            LocalDateTime.of(2021, 11, 13, 12, 0, 0),
            LocalDateTime.of(2021, 11, 13, 14, 0, 0),
            TrainTypes.RUNNING,
            "@van_darkholme"
        ),
        ActivityUsersCard(
            "228 м",
            LocalDateTime.of(2021, 11, 13, 12, 0, 0),
            LocalDateTime.of(2021, 11, 13, 14, 0, 0),
            TrainTypes.CYCLING,
            "@techniquepasha"
        ),
        ActivityUsersCard(
            "10 км",
            LocalDateTime.of(2021, 11, 13, 12, 0, 0),
            LocalDateTime.of(2021, 11, 13, 14, 0, 0),
            TrainTypes.WALKING,
            "@morgen_shtern"
        )
    )

    fun getActivityUsersCards(): List<Card> = cardsList
}