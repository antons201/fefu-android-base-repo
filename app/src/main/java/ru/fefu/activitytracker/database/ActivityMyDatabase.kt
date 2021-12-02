package ru.fefu.activitytracker.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@TypeConverters(Converters::class)
@Database(entities = [ActivityMy::class], version = 1)
abstract class ActivityMyDatabase: RoomDatabase() {
    abstract fun activityMyDao(): ActivityMyDao
}