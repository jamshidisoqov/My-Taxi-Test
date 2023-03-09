package uz.gita.my_taxi_test.domain.repository

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.flow.Flow
import uz.gita.my_taxi_test.data.local.entity.LocationEntity

// Created by Jamshid Isoqov on 3/9/2023
interface LocationRepository {

    suspend fun addLocation(latLng: LatLng, bearing: Double)

    fun getAllLocations(): Flow<List<LocationEntity>>

}