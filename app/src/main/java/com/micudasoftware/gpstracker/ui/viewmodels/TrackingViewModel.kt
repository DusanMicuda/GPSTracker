package com.micudasoftware.gpstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.other.Constants
import com.micudasoftware.gpstracker.other.Event
import com.micudasoftware.gpstracker.other.Utils
import com.micudasoftware.gpstracker.repositories.MainRepositoryImpl
import com.micudasoftware.gpstracker.services.TrackingService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl
) : ViewModel() {

    private val _eventChannel = Channel<Event>()
    val eventChannel = _eventChannel.receiveAsFlow()
    private var mapViewWidth = 0
    private var mapViewHeight = 0
    var btnText = MutableStateFlow("Start")
    var map: GoogleMap? = null
    private var isTracking = false
    private var pathPoints = listOf<LatLng>()
    private var startTime = 0L
    private var stopTime = 0L

    init {
        subscribeToCollectors()
    }

    private fun insertTrack(track: Track) = viewModelScope.launch {
        mainRepository.insertTrack(track)
    }

    private fun subscribeToCollectors() {
        viewModelScope.launch {
            TrackingService.isTracking.collect {
                isTracking = it
                updateTracking(it)
            }
        }

        viewModelScope.launch {
            TrackingService.pathPoints.collect {
                pathPoints = it
                addLatestPolyline()
                moveCameraToUser()
            }
        }

        viewModelScope.launch {
            TrackingService.startTime.collect {
                startTime = it
            }
        }

        viewModelScope.launch {
            TrackingService.stopTime.collect {
                stopTime = it
            }
        }
    }

    fun toggleTrack() {
        if (isTracking) {
            triggerEvent(Event.SendCommandToService(Constants.ACTION_STOP_SERVICE))
            zoomToSeeWholeTrack()
            endTrackAndSaveToDb()
        } else
            triggerEvent(Event.SendCommandToService(Constants.ACTION_START_SERVICE))
    }

    private fun updateTracking(isTracking: Boolean) {
        btnText.value = if (!isTracking)
            "Start"
        else
            "Stop"

    }

    private fun moveCameraToUser() {
        if (pathPoints.isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.last(),
                    Constants.MAP_ZOOM
                )
            )
        }
    }

    private fun endTrackAndSaveToDb() {
        map?.setOnMapLoadedCallback {
            map!!.snapshot { bmp ->
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
                insertTrack(track)
                map!!.clear()
                triggerEvent(Event.ShowToast("Track saved successfully"))
                triggerEvent(Event.Navigate(R.id.action_trackingFragment_to_startFragment))
            }
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (pos in pathPoints)
            bounds.include(pos)

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapViewWidth,
                mapViewHeight / 3,
                (mapViewHeight * 0.05f).toInt()
            )
        )

    }

    fun addAllPolylines() {
        if (isTracking) {
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .addAll(pathPoints)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (isTracking && pathPoints.isNotEmpty() && pathPoints.size > 1) {
            val preLastLatLng = pathPoints[pathPoints.size - 2]
            val lastLatLng = pathPoints.last()
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .add(preLastLatLng)
                .add(lastLatLng)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun triggerEvent(event: Event) = viewModelScope.launch {
        _eventChannel.send(event)
    }

    fun setMapViewSize(width: Int, height: Int) {
        mapViewWidth = width
        mapViewHeight = height
    }
}