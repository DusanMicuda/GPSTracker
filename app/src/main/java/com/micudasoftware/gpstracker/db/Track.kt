package com.micudasoftware.gpstracker.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.micudasoftware.gpstracker.other.Constants.TRACKING_TABLE_NAME

@Entity(tableName = TRACKING_TABLE_NAME)
data class Track(
    val image: Bitmap? = null,
    val dateInMillis: Long = 0L,
    val distanceInMeters: Int = 0,
    val avgSpeedInKMH: Float = 0F,
    val timeInMillis: Long = 0L,
    @PrimaryKey val id: Int? = null
)