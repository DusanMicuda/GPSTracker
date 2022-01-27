package com.micudasoftware.gpstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.other.Constants
import com.micudasoftware.gpstracker.other.Event
import com.micudasoftware.gpstracker.other.Utils
import com.micudasoftware.gpstracker.repositories.MainRepositoryImpl
import com.micudasoftware.gpstracker.ui.screens.destinations.StartScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.round

@ExperimentalPermissionsApi
@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl
) : ViewModel() {

    private val _eventChannel = Channel<Event>()
    val eventChannel = _eventChannel.receiveAsFlow()

    private var mapViewWidth = 0
    private var mapViewHeight = 0

    private val _btnText = MutableStateFlow("Start")
    val btnText = _btnText.asStateFlow()

    var map: GoogleMap? = null

    private val isTracking = mainRepository.isTracking()
    private val pathPoints = mainRepository.getPathPoints()
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
            isTracking.collect {
                updateTracking(it)
            }
        }

        viewModelScope.launch {
            pathPoints.collect {
                addLatestPolyline()
                moveCameraToUser()
            }
        }

        viewModelScope.launch {
            mainRepository.getStartTime().collect {
                startTime = it
            }
        }

        viewModelScope.launch {
            mainRepository.getStopTime().collect {
                stopTime = it
            }
        }
    }

    fun toggleTrack() {
        if (isTracking.value) {
            triggerEvent(Event.SendCommandToService(Constants.ACTION_STOP_SERVICE))
            zoomToSeeWholeTrack()
            endTrackAndSaveToDb()
        } else
            triggerEvent(Event.SendCommandToService(Constants.ACTION_START_SERVICE))
    }

    private fun updateTracking(isTracking: Boolean) {
        _btnText.value = if (!isTracking)
            "Start"
        else
            "Stop"
    }

    private fun moveCameraToUser() {
        if (pathPoints.value.isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathPoints.value.last(),
                    Constants.MAP_ZOOM
                )
            )
        }
    }

    private fun endTrackAndSaveToDb() {
        map?.setOnMapLoadedCallback {
            map!!.snapshot { bmp ->
                val distanceInMeters = Utils.getDistanceInMeters(pathPoints.value).toInt()
                val timeInMillis = stopTime - startTime
                val avgSpeedInKMH =
                    round((distanceInMeters / 1000f) / (timeInMillis / 1000f / 60 / 60) * 10) / 10f
                insertTrack(
                    Track(
                        bmp,
                        startTime,
                        distanceInMeters,
                        avgSpeedInKMH,
                        timeInMillis
                    )
                )

                map!!.clear()
                triggerEvent(Event.ShowToast("Track saved successfully"))
                triggerEvent(Event.SendCommandToService(Constants.ACTION_RESET_SERVICE))
                triggerEvent(Event.Navigate(StartScreenDestination))
            }
        }
    }

    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (pos in pathPoints.value)
            bounds.include(pos)

        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                mapViewWidth,
                mapViewHeight / 4,
                (mapViewHeight * 0.05f).toInt()
            )
        )
    }

    fun addAllPolylines() {
        if (isTracking.value) {
            val polylineOptions = PolylineOptions()
                .color(Constants.POLYLINE_COLOR)
                .width(Constants.POLYLINE_WIDTH)
                .addAll(pathPoints.value)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline() {
        if (isTracking.value && pathPoints.value.isNotEmpty() && pathPoints.value.size > 1) {
            val preLastLatLng = pathPoints.value[pathPoints.value.size - 2]
            val lastLatLng = pathPoints.value.last()
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