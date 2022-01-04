package com.micudasoftware.gpstracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.databinding.FragmentTrackingBinding
import com.micudasoftware.gpstracker.other.Constants.ACTION_START_SERVICE
import com.micudasoftware.gpstracker.other.Constants.ACTION_STOP_SERVICE
import com.micudasoftware.gpstracker.other.Constants.MAP_ZOOM
import com.micudasoftware.gpstracker.other.Constants.POLYLINE_COLOR
import com.micudasoftware.gpstracker.other.Constants.POLYLINE_WIDTH
import com.micudasoftware.gpstracker.services.TrackingService
import com.micudasoftware.gpstracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentTrackingBinding
    private lateinit var map: GoogleMap
    private var isTracking = false
    private var pathPoints = mutableListOf<LatLng>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackingBinding.inflate(layoutInflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it
            addAllPolylines()
        }

        binding.btnToggleTrack.setOnClickListener {
            toggleTrack()
        }

        subscribeToObservers()

        return binding.root
    }

    private fun subscribeToObservers() {
        TrackingService.isTracking.observe(viewLifecycleOwner, {
            isTracking = it
            updateTracking(it)
        })

        TrackingService.pathPoints.observe(viewLifecycleOwner, {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        })
    }

    private fun toggleTrack() {
        if (isTracking)
            sendCommandToService(ACTION_STOP_SERVICE)
        else
            sendCommandToService(ACTION_START_SERVICE)
    }

    private fun updateTracking(isTracking: Boolean) {
        if (!isTracking)
            binding.btnToggleTrack.text = getString(R.string.btnStart)
        else
            binding.btnToggleTrack.text = getString(R.string.btnStop)

    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty()) {
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last(),
                    MAP_ZOOM
                )
            )
        }
    }

    private fun addAllPolylines() {
        val polylineOptions = PolylineOptions()
            .color(POLYLINE_COLOR)
            .width(POLYLINE_WIDTH)
            .addAll(pathPoints)
        map.addPolyline(polylineOptions)
    }

    private fun addLatestPolyline() {
        if (pathPoints.isNotEmpty() && pathPoints.size > 1) {
            val preLastLatLng = pathPoints[pathPoints.size - 2]
            val lastLatLng = pathPoints.last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action: String) =
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }

    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }
}