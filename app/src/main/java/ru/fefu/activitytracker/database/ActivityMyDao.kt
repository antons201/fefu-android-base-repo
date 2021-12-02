package ru.fefu.activitytracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityMyDao {
    @Query("SELECT * FROM activityMy")
    fun getAll(): LiveData<List<ActivityMy>>

    @Query("SELECT * FROM activityMy WHERE id=:id")
    fun getById(id: Int): ActivityMy

    @Insert
    fun insert(activity: ActivityMy)

    @Insert
    fun insertAll(vararg activity: ActivityMy)

    @Delete
    fun delete(activity: ActivityMy)
}