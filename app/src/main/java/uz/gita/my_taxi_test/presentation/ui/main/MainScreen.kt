package uz.gita.my_taxi_test.presentation.ui.main

import android.Manifest
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.Symbol
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions
import com.mapbox.mapboxsdk.utils.BitmapUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import uz.gita.my_taxi_test.R
import uz.gita.my_taxi_test.databinding.ScreenMainBinding
import uz.gita.my_taxi_test.presentation.presenter.MainViewModel
import uz.gita.my_taxi_test.presentation.presenter.impl.MainViewModelImpl
import uz.gita.my_taxi_test.service.LocationListenerService
import uz.gita.my_taxi_test.utils.extensions.checkLocation
import uz.gita.my_taxi_test.utils.extensions.hasPermission
import uz.gita.my_taxi_test.utils.extensions.include
import uz.gita.my_taxi_test.utils.latLngEvaluator


// Created by Jamshid Isoqov on 3/8/2023
@AndroidEntryPoint
class MainScreen : Fragment(R.layout.screen_main), OnMapReadyCallback {

    private lateinit var viewBinding: ScreenMainBinding

    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()

    private var mapBox: MapboxMap? = null

    private lateinit var symbolManager: SymbolManager

    private lateinit var markerIcon: Symbol

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

        viewBinding =
            ScreenMainBinding.bind(inflater.inflate(R.layout.screen_main, container, false))

        viewBinding.mainContent.mainMapView.onCreate(savedInstanceState)

        return viewBinding.root
    }

    @SuppressLint("Recycle")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = viewBinding.include {
        setUpObservers()
        startLocation()
        mainContent.mainMapView.getMapAsync(this@MainScreen)
    }

    private fun setUpObservers() {
        viewModel.lastLocationFlow.onEach { location ->
            mapBox?.let { mapboxMap ->
                val camera = CameraUpdateFactory.newCameraPosition(
                    CameraPosition.Builder()
                        .target(LatLng(location.lat, location.lng))
                        .bearing(location.bearing)
                        .build()
                )
                mapboxMap.animateCamera(camera)

                val anim = ObjectAnimator.ofObject(
                    latLngEvaluator,
                    markerIcon.latLng,
                    LatLng(location.lat, location.lng)
                ).setDuration(2000L)
                anim.addUpdateListener {
                    markerIcon.latLng = it.animatedValue as LatLng
                    symbolManager.update(markerIcon)
                }
                anim.start()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)
    }


    @SuppressLint("NewApi")
    private fun startLocation() {
        hasPermission(Manifest.permission.ACCESS_FINE_LOCATION, onPermissionGranted = {
            hasPermission(Manifest.permission.FOREGROUND_SERVICE, onPermissionGranted = {
                requireContext().checkLocation {
                    if (it) {
                        val intent = Intent(requireContext(), LocationListenerService::class.java)
                        requireContext().startForegroundService(intent)
                    }
                }
            }) {}

        }, onPermissionDenied = {
            Snackbar.make(
                viewBinding.mainContent.containerFree,
                "Location not allowed. Please check again",
                Snackbar.LENGTH_SHORT
            ).setAction("Try check") {
                startLocation()
            }.show()
        })
    }

    override fun onStop() {
        super.onStop()
        viewBinding.mainContent.mainMapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        viewBinding.mainContent.mainMapView.onLowMemory()
    }

    override fun onStart() {
        super.onStart()
        viewBinding.mainContent.mainMapView.onStart()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.mainContent.mainMapView.onPause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding.mainContent.mainMapView.onDestroy()
    }

    override fun onMapReady(mapbox: MapboxMap) = viewBinding.include {
        mapbox.uiSettings.apply {
            isCompassEnabled = false
            isLogoEnabled = false
            isAttributionEnabled = false
        }

        val style =
            when (requireContext().resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    Style.LIGHT
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    Style.DARK
                }
                else -> Style.LIGHT
            }

        mapbox.setStyle(style) { mapStyle ->
            val selectedMarkerIconDrawable =
                ResourcesCompat.getDrawable(resources, R.drawable.ic_yellow_car, null)
            mapBox = mapbox
            mapbox.style?.addImage(
                CAR_MARKER,
                BitmapUtils.getBitmapFromDrawable(selectedMarkerIconDrawable)!!
            )
            symbolManager = SymbolManager(mainContent.mainMapView, mapbox, mapStyle)
            symbolManager.iconAllowOverlap = true
            symbolManager.iconIgnorePlacement = true
            markerIcon = symbolManager.create(
                SymbolOptions()
                    .withLatLng(TASHKENT)
                    .withIconImage(CAR_MARKER)
                    .withDraggable(false)
            )

            mapbox.cameraPosition = CameraPosition.Builder()
                .target(TASHKENT)
                .zoom(15.0)
                .build()
        }
    }

    companion object {
        const val CAR_MARKER = "car_marker"
        val TASHKENT = LatLng(41.2995, 69.2401)
    }
}

