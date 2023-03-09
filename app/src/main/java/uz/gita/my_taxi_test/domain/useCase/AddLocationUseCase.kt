package uz.gita.my_taxi_test.domain.useCase

import com.mapbox.mapboxsdk.geometry.LatLng

// Created by Jamshid Isoqov on 3/9/2023
interface AddLocationUseCase {

    suspend fun addLocation(latLng: LatLng,bearing:Double)

}