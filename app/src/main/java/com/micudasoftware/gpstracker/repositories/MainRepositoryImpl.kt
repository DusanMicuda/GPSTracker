package com.micudasoftware.gpstracker.repositories

import com.google.android.gms.maps.model.LatLng
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.db.TrackDAO
import com.micudasoftware.gpstracker.services.TrackingService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val trackDao: TrackDAO
) : MainRepository{

    override suspend fun insertTrack(track: Track) = trackDao.insertTrack(track)

    override suspend fun deleteTrack(track: Track) = trackDao.deleteTrack(track)

    override fun getAllTracksSortedByDate() = trackDao.getAllTracksSortedByDate()

    override fun isTracking() = TrackingService.isTracking

    override fun getPathPoints() = TrackingService.pathPoints

    override fun getStartTime() = TrackingService.startTime

    override fun getStopTime() = TrackingService.stopTime
}