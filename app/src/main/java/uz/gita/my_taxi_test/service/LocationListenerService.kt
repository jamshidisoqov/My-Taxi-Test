package uz.gita.my_taxi_test.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import uz.gita.my_taxi_test.data.local.dao.LocationDao
import uz.gita.my_taxi_test.data.local.entity.LocationEntity
import javax.inject.Inject

// Created by Jamshid Isoqov on 3/9/2023
@AndroidEntryPoint
class LocationListenerService : Service(), LocationListener {

    @Inject
    lateinit var dao: LocationDao

    private val scope = CoroutineScope(Dispatchers.IO + Job())

    private var locationManager: LocationManager? = null

    override fun onBind(p0: Intent?): IBinder? = null

    @SuppressLint("MissingPermission")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        locationManager?.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            5000L,
            10f,
            this
        )
        return START_STICKY
    }

    override fun onLocationChanged(p0: Location) {
        scope.launch {
            dao.insertLocation(LocationEntity(0, p0.latitude, p0.longitude, p0.bearing.toDouble()))
        }
    }
}