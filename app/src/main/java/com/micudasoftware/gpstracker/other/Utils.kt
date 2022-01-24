package com.micudasoftware.gpstracker.other

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import java.util.concurrent.TimeUnit

object Utils {

    fun getDistanceInMeters(pathPoints: List<LatLng>) : Float{
        var distanceInMeters = 0f
        for (i in 0..(pathPoints.size - 2)) {
            val pos1 = pathPoints[i]
            val pos2 = pathPoints[i+1]

            val result = FloatArray(1)
            Location.distanceBetween(
                pos1.latitude,
                pos1.longitude,
                pos2.latitude,
                pos2.longitude,
                result
            )
            distanceInMeters += result[0]
        }
        return distanceInMeters
    }

    fun getFormattedTime(ms: Long) : String{
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }

}