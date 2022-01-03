package com.micudasoftware.gpstracker.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.google.android.gms.location.Geofence

@Dao
interface TrackDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)

    @Delete
    suspend fun deleteTrack(track: Track)

    @Query("SELECT * FROM tracking_table ORDER BY dateInMillis DESC")
    fun getAllTracksSortedByDate(): LiveData<List<Track>>

}