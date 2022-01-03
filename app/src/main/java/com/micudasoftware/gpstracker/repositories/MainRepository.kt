package com.micudasoftware.gpstracker.repositories

import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.db.TrackDAO
import javax.inject.Inject

class MainRepository @Inject constructor(
    val trackDao: TrackDAO
) {
    suspend fun insertTrack(track: Track) = trackDao.insertTrack(track)

    suspend fun deleteTrack(track: Track) = trackDao.deleteTrack(track)

    fun getAllTracksSortedByDate() = trackDao.getAllTracksSortedByDate()
}