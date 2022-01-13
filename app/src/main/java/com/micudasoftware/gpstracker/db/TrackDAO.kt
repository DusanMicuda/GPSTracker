package com.micudasoftware.gpstracker.db

import androidx.room.*
import com.micudasoftware.gpstracker.other.Constants.TRACKING_TABLE_NAME
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)

    @Delete
    suspend fun deleteTrack(track: Track)

    @Query("SELECT * FROM $TRACKING_TABLE_NAME ORDER BY dateInMillis DESC")
    fun getAllTracksSortedByDate(): Flow<List<Track>>

}