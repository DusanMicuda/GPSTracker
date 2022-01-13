package com.micudasoftware.gpstracker.repositories

import com.micudasoftware.gpstracker.db.Track
import kotlinx.coroutines.flow.Flow

interface MainRepository {

    suspend fun insertTrack(track: Track)

    suspend fun deleteTrack(track: Track)

    fun getAllTracksSortedByDate(): Flow<List<Track>>
}