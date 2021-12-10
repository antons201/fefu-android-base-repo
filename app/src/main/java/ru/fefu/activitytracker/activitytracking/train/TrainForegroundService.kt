package ru.fefu.activitytracker.activitytracking.train

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import ru.fefu.activitytracker.App
import ru.fefu.activitytracker.R
import ru.fefu.activitytracker.activitytracking.cards.utils.DistanceUtils.getDistanceByString
import java.time.LocalDateTime

class TrainForegroundService : Service() {
    companion object {
        private const val TAG = "SERVICE"
        private const val CHANNEL_ID = "foreground_service_id"
        private const val EXTRA_ID = "id"

        const val ACTION_START = "start"
        const val ACTION_CANCEL = "cancel"

        var distance = 0.0
        var activityId = -1
        val coordinates = mutableListOf<Pair<Double, Double>>()

        val locationRequest: LocationRequest
            get() = LocationRequest.create()
                .setInterval(5000L)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(5f)

        fun startForeground(context: Context, id: Int) {
            activityId = id
            val intent = Intent(context, TrainForegroundService::class.java)
            intent.putExtra(EXTRA_ID, activityId)
            ContextCompat.startForegroundService(context, intent)
            intent.action = ACTION_START
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    private var locationCallback: LocationCallback? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (intent?.action == ACTION_CANCEL) {

            App.INSTANCE.db.activityMyDao().updateEndTime(activityId, LocalDateTime.now())
            App.INSTANCE.db.activityMyDao().updateDistance(
                activityId,
                getDistanceByString(distance))
            App.INSTANCE.db.activityMyDao().updateFinished(activityId, true)

            distance = 0.0
            activityId = -1
            coordinates.clear()

            stopLocationUpdates()
            stopForeground(true)
            stopSelf()
            return START_NOT_STICKY
        } else if (intent?.action == ACTION_START) {
            startLocationUpdates(intent.getIntExtra(EXTRA_ID, -1))
            return START_REDELIVER_INTENT
        }

        return START_NOT_STICKY
    }

    private fun startLocationUpdates(id: Int) {
        if (id == -1) stopSelf()

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_DENIED
        ) stopSelf()

        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        ActivityLocationCallback(id).apply {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                this,
                Looper.getMainLooper()
            )
            locationCallback = this
        }
        showNotification()
    }

    private fun stopLocationUpdates() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    private fun showNotification() {
        createChannel()
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, TrainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val cancelIntent = Intent(this, TrainForegroundService::class.java).apply {
            action = ACTION_CANCEL
        }
        val cancelPendingIntent = PendingIntent.getService(
            this,
            1,
            cancelIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Train")
            .setContentText("Tracking your activity")
            .setSmallIcon(R.drawable.ic_baseline_add)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_baseline_stop, "Stop", cancelPendingIntent)
            .build()
        startForeground(1, notification)
    }

    private fun createChannel() {
        val channel = NotificationChannel(CHANNEL_ID, "Default channel", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    override fun onDestroy() {
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
        super.onDestroy()
    }


    inner class ActivityLocationCallback(private val activityId: Int) : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            val lastLocation = result?.lastLocation ?: return

            val newCoordinates = Pair(lastLocation.latitude, lastLocation.longitude)
            coordinates.add(newCoordinates)

            App.INSTANCE.db.activityMyDao().updateCoordinatesList(activityId, coordinates)

            if (coordinates.size > 1) {
                val oldCoordinates = coordinates[coordinates.lastIndex - 1]

                val currentLocation = Location("CurrentLocation")
                currentLocation.latitude = newCoordinates.first
                currentLocation.longitude = newCoordinates.second

                val prevLocation = Location("PrevLocation")
                prevLocation.latitude = oldCoordinates.first
                prevLocation.longitude = oldCoordinates.second

                distance += currentLocation.distanceTo(prevLocation)
            }
        }
    }
}