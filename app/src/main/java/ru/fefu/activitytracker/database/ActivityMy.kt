package ru.fefu.activitytracker.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.fefu.activitytracker.activitytracking.train.TrainTypes
import java.time.LocalDateTime

@Entity
data class ActivityMy (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "start_time") val start_time : LocalDateTime,
    @ColumnInfo(name = "end_time") val end_time : LocalDateTime,
    @ColumnInfo(name = "sport_type") val sport_type : TrainTypes,
    @ColumnInfo(name = "coordinates_list") val coordinates_list: List<Pair<Double, Double>>
)
