package com.micudasoftware.gpstracker.services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Looper
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.model.LatLng
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.other.Constants.ACTION_RESET_SERVICE
import com.micudasoftware.gpstracker.other.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import com.micudasoftware.gpstracker.other.Constants.ACTION_START_SERVICE
import com.micudasoftware.gpstracker.other.Constants.ACTION_STOP_SERVICE
import com.micudasoftware.gpstracker.other.Constants.FASTEST_LOCATION_UPDATE_INTERVAL
import com.micudasoftware.gpstracker.other.Constants.LOCATION_UPDATE_INTERVAL
import com.micudasoftware.gpstracker.other.Constants.NOTIFICATION_CHANNEL_ID
import com.micudasoftware.gpstracker.other.Constants.NOTIFICATION_CHANNEL_NAME
import com.micudasoftware.gpstracker.other.Constants.NOTIFICATION_ID
import com.micudasoftware.gpstracker.other.Utils
import com.micudasoftware.gpstracker.ui.MainActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.*


class TrackingService : LifecycleService() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        private val _isTracking = MutableStateFlow(false)
        val isTracking = _isTracking.asStateFlow()
        private val _pathPoints = MutableStateFlow<MutableList<LatLng>>(arrayListOf())
        val pathPoints = _pathPoints.asStateFlow()
        private val _startTime = MutableStateFlow(0L)
        val startTime = _startTime.asStateFlow()
        private val _stopTime = MutableStateFlow(0L)
        val stopTime = _stopTime.asStateFlow()
    }

    override fun onCreate() {
        super.onCreate()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        lifecycleScope.launch {
            isTracking.collect {
                updateLocationTracking(it)
            }
        }
    }

    private fun killService() {
        _isTracking.value = false
        _stopTime.value = Calendar.getInstance().timeInMillis
        stopForeground(true)
        stopSelf()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    startForegroundService()
                }
                ACTION_STOP_SERVICE -> {
                    killService()
                }
                ACTION_RESET_SERVICE -> {
                    _pathPoints.value = mutableListOf()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        if (isTracking) {
            if (Utils.hasLocationPermissions(this)) {
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                    isWaitForAccurateLocation = true
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
            }
        } else
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)
            if (isTracking.value)
                addPathPoint(result.lastLocation)
        }
    }

    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            _pathPoints.value = pathPoints.value.plus(pos).toMutableList()
        }
    }

    private fun startForegroundService() {
        _isTracking.value = true
        _startTime.value = Calendar.getInstance().timeInMillis

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createNotificationChannel(notificationManager)

        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_place)
            .setContentTitle("GPS Tracker")
            .setContentText("Running...")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() : PendingIntent {
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            FLAG_IMMUTABLE or FLAG_UPDATE_CURRENT
        else
            FLAG_UPDATE_CURRENT

        return PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).also {
                it.action = ACTION_SHOW_TRACKING_FRAGMENT
            },
            flags
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}