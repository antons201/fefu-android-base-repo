package ru.fefu.activitytracker.activitytracking

import ru.fefu.activitytracker.activitytracking.train.TrainTypes
import java.time.LocalDateTime

data class ActivityMyCard (
    val id: Int,
    val distance : String,
    val start_time : LocalDateTime,
    val stop_time : LocalDateTime,
    val sport_type : TrainTypes,
) : Card
