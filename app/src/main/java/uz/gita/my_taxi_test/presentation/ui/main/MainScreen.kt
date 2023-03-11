package uz.gita.my_taxi_test.presentation.ui.main

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.TypeEvaluator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
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

    private lateinit var viewBinding: ScreenMainBinding

    private val viewModel: MainViewModel by viewModels<MainViewModelImpl>()

    private var mapBox: MapboxMap? = null

    private lateinit var marker: Marker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

        viewBinding =
            ScreenMainBinding.bind(inflater.inflate(R.layout.screen_main, container, false))

        viewBinding.mainContent.mainMapView.onCreate(savedInstanceState)

        return viewBinding.root
    }

    @SuppressLint("Recycle")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = viewBinding.include {

        val markerOption = MarkerOptions().apply {
            position = TASHKENT
            icon = IconFactory.getInstance(requireContext())
                .fromBitmap(bitmapFromDrawableRes(R.drawable.ic_yellow_car)!!)
        }
        mainContent.mainMapView.getMapAsync {
            it.setStyle(Style.MAPBOX_STREETS)
            it.cameraPosition = CameraPosition.Builder()
                .target(TASHKENT)
                .zoom(15.0)
                .build()
            mapBox = it

            this@MainScreen.marker = mapBox?.addMarker(markerOption)!!
        }

        viewModel.lastLocationFlow.onEach { location ->
            mapBox?.let { mapboxMap ->
                mapboxMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(LatLng(location.lat, location.lng))
                            .bearing(location.bearing)
                            .build()
                    )
                )
                val animator = ObjectAnimator.ofObject(
                    latLngEvaluator, this@MainScreen.marker.position,
                    LatLng(location.lat, location.lng)
                ).setDuration(1500L)
                animator.addUpdateListener {
                    this@MainScreen.marker.position = it.animatedValue as LatLng
                }
                animator.start()
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        startLocation()

    }

    private val latLngEvaluator: TypeEvaluator<LatLng> = object : TypeEvaluator<LatLng> {
        private val latLng = LatLng()
        override fun evaluate(fraction: Float, startValue: LatLng, endValue: LatLng): LatLng {
            latLng.latitude =
                startValue.latitude + (endValue.latitude - startValue.latitude) * fraction
            latLng.longitude =
                startValue.longitude + (endValue.longitude - startValue.longitude) * fraction
            return latLng
        }
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
                    "Location not allowed. Please check again",
                    Snackbar.LENGTH_SHORT
                )
                    .setAction("Try check") {
                        startLocation()
                    }
                    .show()
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

    companion object {
        val TASHKENT = LatLng(41.2995, 69.2401)
    }
}

