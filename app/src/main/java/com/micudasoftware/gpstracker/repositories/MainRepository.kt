package com.micudasoftware.gpstracker.repositories

import com.google.android.gms.maps.model.LatLng
import com.micudasoftware.gpstracker.db.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MainRepository {

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun getAllTracksSortedByDate(): Flow<List<Track>>

    fun isTracking(): StateFlow<Boolean>

    fun getPathPoints(): StateFlow<MutableList<LatLng>>

    fun getStartTime(): StateFlow<Long>

    fun getStopTime(): StateFlow<Long>
}