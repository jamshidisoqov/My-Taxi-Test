package uz.gita.my_taxi_test.presentation.presenter

import kotlinx.coroutines.flow.SharedFlow
import uz.gita.my_taxi_test.data.local.entity.LocationEntity

// Created by Jamshid Isoqov on 3/9/2023
interface MainViewModel {

    val lastLocationFlow: SharedFlow<LocationEntity>

}