package uz.gita.my_taxi_test.domain.useCase.impl

import android.annotation.SuppressLint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import uz.gita.my_taxi_test.data.local.entity.LocationEntity
import uz.gita.my_taxi_test.domain.repository.LocationRepository
import uz.gita.my_taxi_test.domain.useCase.GetLastLocationUseCase
import javax.inject.Inject

class GetLastLocationUseCaseImpl @Inject constructor(
    private val repository: LocationRepository
) : GetLastLocationUseCase {
    @SuppressLint("SuspiciousIndentation")
    override fun getLastLocation(): Flow<LocationEntity> = callbackFlow {
        repository.getAllLocations().onEach {
            if (it.isNotEmpty())
                trySend(it.last())
        }.launchIn(this)
        awaitClose()
    }.flowOn(Dispatchers.Default)
}