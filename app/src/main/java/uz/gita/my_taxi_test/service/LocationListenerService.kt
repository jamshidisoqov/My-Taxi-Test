package uz.gita.my_taxi_test.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.mapbox.android.core.location.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.gita.my_taxi_test.R
import uz.gita.my_taxi_test.data.local.dao.LocationDao
import uz.gita.my_taxi_test.data.local.entity.LocationEntity
import javax.inject.Inject

// Created by Jamshid Isoqov on 3/9/2023
@AndroidEntryPoint
class LocationListenerService : Service() {

    @Inject
    lateinit var dao: LocationDao

    private lateinit var locationEngine: LocationEngine

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    companion object {
        private const val CHANNEL_ID = "location_channel"
        private const val NOTIFICATION_ID = 7
        private const val CHANNEL_NAME = "Location"
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
        locationEngine = LocationEngineProvider.getBestLocationEngine(this)
    }

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val request = LocationEngineRequest
            .Builder(3000L)
            .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
            .setMaxWaitTime(1000L)
            .build()

        locationEngine.requestLocationUpdates(
            request,
            object : LocationEngineCallback<LocationEngineResult> {
                override fun onSuccess(result: LocationEngineResult?) {
                    result?.lastLocation?.let { location ->
                        scope.launch {
                            dao.insertLocation(
                                LocationEntity(
                                    0,
                                    location.latitude,
                                    location.longitude,
                                    location.bearing.toDouble()
                                )
                            )
                        }
                    }
                }

                override fun onFailure(p0: Exception) {}
            },
            Looper.getMainLooper()
        )
        createNotification()
        return START_STICKY
    }

    private fun createNotification() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location")
            .setContentText("Getting your location...")
            .setSmallIcon(R.drawable.ic_location)
            .build()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}