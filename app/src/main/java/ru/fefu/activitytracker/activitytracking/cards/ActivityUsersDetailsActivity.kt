package ru.fefu.activitytracker.activitytracking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import ru.fefu.activitytracker.R

class ActivityUsersDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users_details)

        val toolbar = findViewById<Toolbar>(R.id.activity_users_details_toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}