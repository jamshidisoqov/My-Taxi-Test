package uz.gita.my_taxi_test.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
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
class LocationListenerService : Service(), LocationListener {

    @Inject
    lateinit var dao: LocationDao

    private lateinit var locationEngine: LocationEngine

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private var locationManager: LocationManager? = null

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

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000L,
            0f,
            this
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

    override fun onLocationChanged(p0: Location) {
        scope.launch {
            dao.insertLocation(LocationEntity(0, p0.latitude, p0.longitude, p0.bearing.toDouble()))
        }
    }
}