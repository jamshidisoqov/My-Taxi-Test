package uz.gita.my_taxi_test.utils

import android.animation.TypeEvaluator
import com.mapbox.mapboxsdk.geometry.LatLng

// Created by Jamshid Isoqov on 3/13/2023
val latLngEvaluator: TypeEvaluator<LatLng> = object : TypeEvaluator<LatLng> {
    private val latLng = LatLng()
    override fun evaluate(fraction: Float, startValue: LatLng, endValue: LatLng): LatLng {
        latLng.latitude =
            startValue.latitude + (endValue.latitude - startValue.latitude) * fraction
        latLng.longitude =
            startValue.longitude + (endValue.longitude - startValue.longitude) * fraction
        return latLng
    }
}