package uz.gita.my_taxi_test.domain.repository.impl

import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import uz.gita.my_taxi_test.data.local.dao.LocationDao
import uz.gita.my_taxi_test.data.local.entity.LocationEntity
import uz.gita.my_taxi_test.domain.repository.LocationRepository
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val dao: LocationDao
) : LocationRepository {
    override suspend fun addLocation(latLng: LatLng,bearing:Double) =
        dao.insertLocation(LocationEntity(0, latLng.latitude, latLng.longitude,bearing))

    override fun getAllLocations(): Flow<List<LocationEntity>> =
        dao.getAllLocation().flowOn(Dispatchers.IO)
}