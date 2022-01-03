package com.micudasoftware.gpstracker.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracking_table")
data class Track(
    var image: Bitmap? = null,
    var dateInMillis: Long = 0L,
    var distanceInMeters: Int = 0,
    var avgSpeedInKMH: Float = 0F,
    var timeInMillis: Long = 0L
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}