package com.micudasoftware.gpstracker.repositories

import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.db.TrackDAO
import javax.inject.Inject

class MainRepositoryImpl @Inject constructor(
    private val trackDao: TrackDAO
) : MainRepository{

    override suspend fun insertTrack(track: Track) = trackDao.insertTrack(track)

    override suspend fun deleteTrack(track: Track) = trackDao.deleteTrack(track)

    override fun getAllTracksSortedByDate() = trackDao.getAllTracksSortedByDate()
}