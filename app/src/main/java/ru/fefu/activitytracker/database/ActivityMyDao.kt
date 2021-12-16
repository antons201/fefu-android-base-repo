package ru.fefu.activitytracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import org.osmdroid.util.Distance
import java.time.LocalDateTime

@Dao
interface ActivityMyDao {
    @Query("SELECT * FROM activityMy where finished=1")
    fun getAll(): LiveData<List<ActivityMy>>

    @Query("SELECT * FROM activityMy WHERE id=:id")
    fun getById(id: Int): ActivityMy

    @Insert
    fun insert(activityMy: ActivityMy): Long

    @Insert
    fun insertAll(vararg activityMy: ActivityMy)

    @Delete
    fun delete(activityMy: ActivityMy)

    @Update
    fun update(activityMy: ActivityMy)

    @Update
    fun updateAll(vararg activityMy: ActivityMy)

    @Query("SELECT * FROM activityMy ORDER by id DESC LIMIT 1")
    fun getLastTrain(): ActivityMy?

    @Query("SELECT * FROM activityMy WHERE id=:id LIMIT 1")
    fun getLiveData(id: Int): LiveData<ActivityMy>

    @Query("UPDATE activityMy SET coordinates_list=:coordinatesList WHERE id=:id")
    fun updateCoordinatesList(id: Int, coordinatesList: List<Pair<Double, Double>>)

    @Query("UPDATE ActivityMy SET finished=:finished WHERE id=:id")
    fun updateFinished(id: Int, finished: Boolean)

    @Query("UPDATE ActivityMy SET end_time=:endTime WHERE id=:id")
    fun updateEndTime(id: Int, endTime: LocalDateTime)
}