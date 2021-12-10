package ru.fefu.activitytracker.activitytracking.train


import android.os.Bundle
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import org.osmdroid.config.Configuration
import org.osmdroid.util.BoundingBox
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.CustomZoomButtonsController
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.databinding.ActivityTrainBinding

class TrainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        binding = ActivityTrainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                if (App.INSTANCE.db.activityMyDao().getLastTrain() == null) {
                    add(R.id.train_info, NewTrainFragment.newInstance(), NewTrainFragment.TAG)
                } else {
                    if (App.INSTANCE.db.activityMyDao().getLastTrain()?.finished as Boolean) {
                        add(R.id.train_info, NewTrainFragment.newInstance(), NewTrainFragment.TAG)
                    } else {
                        add(
                            R.id.train_info, ActiveTrainFragment.newInstance(
                                App.INSTANCE.db.activityMyDao()
                                    .getLastTrain()?.sport_type as TrainTypes,
                                App.INSTANCE.db.activityMyDao().getLastTrain()?.id as Int,
                                true
                            ),
                            ActiveTrainFragment.TAG
                        )
                    }
                }
                commit()
            }
        }

        Configuration.getInstance().load(this, getPreferences(Context.MODE_PRIVATE))
        initMap()

        val toolbar = binding.activityMyDetailsToolbar

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onPause() {
        binding.mapView.onPause()
        super.onPause()
    }

    private fun initMap() {
        binding.mapView.setTileSource(TileSourceFactory.MAPNIK)
        binding.mapView.setMultiTouchControls(true)
        binding.mapView.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        binding.mapView.minZoomLevel = 4.0
        binding.mapView.post {
            binding.mapView.zoomToBoundingBox(
                BoundingBox(
                    43.232111,
                    132.117062,
                    42.968866,
                    131.768039
                ),
                false
            )
        }
    }

}