package ru.fefu.activitytracker.activitytracking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import ru.fefu.activitytracker.R

class ActivityMyDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_details)

        val toolbar = findViewById<Toolbar>(R.id.activity_my_details_toolbar)

        toolbar.setNavigationOnClickListener {
            finish()
        }

    }

}