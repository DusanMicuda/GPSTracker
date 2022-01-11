package com.micudasoftware.gpstracker.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.databinding.FragmentTrackingBinding
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.other.Constants.ACTION_START_SERVICE
import com.micudasoftware.gpstracker.other.Constants.ACTION_STOP_SERVICE
import com.micudasoftware.gpstracker.other.Constants.MAP_ZOOM
import com.micudasoftware.gpstracker.other.Constants.POLYLINE_COLOR
import com.micudasoftware.gpstracker.other.Constants.POLYLINE_WIDTH
import com.micudasoftware.gpstracker.other.Utils
import com.micudasoftware.gpstracker.services.TrackingService
import com.micudasoftware.gpstracker.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {

    private val viewModel: MainViewModel by viewModels()
    private lateinit var binding: FragmentTrackingBinding
    private lateinit var map: GoogleMap
    private var isTracking = false
    private var pathPoints = mutableListOf<LatLng>()
    private var startTime = 0L
    private var stopTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTrackingBinding.inflate(layoutInflater, container, false)

        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.getMapAsync {
            map = it

            subscribeToObservers()
            addAllPolylines()
        }

        binding.btnToggleTrack.setOnClickListener {
            toggleTrack()
        }

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

        TrackingService.startTime.observe(viewLifecycleOwner, {
            startTime = it
        })

        TrackingService.stopTime.observe(viewLifecycleOwner, {
            stopTime = it
        })
    }

    private fun toggleTrack() {
        if (isTracking) {
            sendCommandToService(ACTION_STOP_SERVICE)
            zoomToSeeWholeTrack()
            endTrackAndSaveToDb()
        } else
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

    private fun endTrackAndSaveToDb() {
        map.setOnMapLoadedCallback {
            map.snapshot { bmp ->
                val distanceInMeters = Utils.getDistanceInMeters(pathPoints).toInt()
                val timeInMillis = stopTime - startTime
                val avgSpeedInKMH =
                    round((distanceInMeters / 1000f) / (timeInMillis / 1000f / 60 / 60) * 10) / 10f
                val track = Track(
                    bmp,
                    startTime,
                    distanceInMeters,
                    avgSpeedInKMH,
                    timeInMillis
                )
                viewModel.insertTrack(track)
                TrackingService.pathPoints.postValue(mutableListOf())
                map.clear()
                Toast.makeText(requireActivity(), "Track saved successfully", LENGTH_SHORT).show()
                binding.root.findNavController().navigate(R.id.action_trackingFragment_to_startFragment)
            }
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (pos in pathPoints)
            bounds.include(pos)

        map.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height / 3,
                (binding.mapView.height * 0.05f).toInt()
            )
        )

    }

    private fun addAllPolylines() {
        if (isTracking) {
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(pathPoints)
            map.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (isTracking && pathPoints.isNotEmpty() && pathPoints.size > 1) {
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