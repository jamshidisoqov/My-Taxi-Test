package uz.gita.my_taxi_test.presentation.ui.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.my_taxi_test.R
import uz.gita.my_taxi_test.databinding.ScreenMainBinding
import uz.gita.my_taxi_test.presentation.presenter.MainViewModel
import uz.gita.my_taxi_test.presentation.presenter.impl.MainViewModelImpl
import uz.gita.my_taxi_test.service.LocationListenerService
import uz.gita.my_taxi_test.utils.extensions.bitmapFromDrawableRes
import uz.gita.my_taxi_test.utils.extensions.hasPermission
import uz.gita.my_taxi_test.utils.extensions.include

// Created by Jamshid Isoqov on 3/8/2023
@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main) {

    private val viewBinding: ScreenMainBinding by viewBinding(ScreenMainBinding::bind)

    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()

    private var mapBox: MapboxMap? = null

    private lateinit var markerOptions: MarkerOptions

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = viewBinding.include {

        markerOptions = MarkerOptions().apply {
            position = TASHKENT
            icon = IconFactory.getInstance(requireContext())
                .fromBitmap(bitmapFromDrawableRes(R.drawable.ic_yellow_car)!!)
        }

        mainContent.mainMapView.getMapAsync {
            mapBox = it!!
            mapBox?.addMarker(markerOptions)
        }

        viewModel.lastLocationFlow.onEach { location ->
            mapBox?.let { mapboxMap ->
                mapboxMap.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(location.lat, location.lng))
                    .bearing(location.bearing)
                    .zoom(15.0)
                    .build()
                mapboxMap.addMarker(markerOptions)
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        startLocation()

    }

    private fun startLocation() {
        hasPermission(
            Manifest.permission.ACCESS_FINE_LOCATION,
            onPermissionGranted = {
                val intent = Intent(requireContext(), LocationListenerService::class.java)
                requireContext().startService(intent)
            },
            onPermissionDenied = {
                Snackbar.make(
                    viewBinding.mainContent.containerFree,
                    "Try check",
                    Snackbar.LENGTH_SHORT
                )
                    .setAction("Try check") {
                        startLocation()
                    }
                    .show()
            })
    }

    override fun onStart() {
        super.onStart()
        viewBinding.mainContent.mainMapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewBinding.mainContent.mainMapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        viewBinding.mainContent.mainMapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.mainContent.mainMapView.onDestroy()
    }

    companion object {
        val TASHKENT = LatLng(41.2995, 69.2401)
    }
}