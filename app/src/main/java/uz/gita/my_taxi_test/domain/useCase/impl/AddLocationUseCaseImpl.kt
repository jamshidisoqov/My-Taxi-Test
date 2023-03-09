package uz.gita.my_taxi_test.domain.useCase.impl

import com.mapbox.mapboxsdk.geometry.LatLng
import uz.gita.my_taxi_test.domain.repository.LocationRepository
import uz.gita.my_taxi_test.domain.useCase.AddLocationUseCase
import javax.inject.Inject

class AddLocationUseCaseImpl @Inject constructor(
    private val repository: LocationRepository
) : AddLocationUseCase {
    override suspend fun addLocation(latLng: LatLng, bearing: Double) =
        repository.addLocation(latLng, bearing)
}