package uz.gita.my_taxi_test.domain.useCase

import kotlinx.coroutines.flow.Flow
import uz.gita.my_taxi_test.data.local.entity.LocationEntity

// Created by Jamshid Isoqov on 3/9/2023
interface GetLastLocationUseCase {

    fun getLastLocation(): Flow<LocationEntity>

}