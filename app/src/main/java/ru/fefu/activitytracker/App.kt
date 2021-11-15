package ru.fefu.activitytracker

import android.app.Application
import androidx.room.Room
import ru.fefu.activitytracker.database.ActivityMyDatabase

class App : Application() {

    companion object {
        lateinit var INSTANCE: App
    }

    val db: ActivityMyDatabase by lazy {
        Room.databaseBuilder(
            this,
            ActivityMyDatabase::class.java,
            "my_activity_database"
        ).allowMainThreadQueries().build()
    }

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this
    }

}