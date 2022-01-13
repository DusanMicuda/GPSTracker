package com.micudasoftware.gpstracker.other

import android.graphics.Color

object Constants {

    const val TRACK_DATABASE_NAME = "track_db"
    const val TRACKING_TABLE_NAME = "tracking_table"

    const val REQUEST_CODE_LOCATION_PERMISSION = 0
    const val REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION = 1

    const val ACTION_START_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val ACTION_SHOW_TRACKING_FRAGMENT = "ACTION_SHOW_TRACKING_FRAGMENT"

    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_UPDATE_INTERVAL = 2000L

    const val POLYLINE_COLOR = Color.BLUE
    const val POLYLINE_WIDTH = 8F
    const val MAP_ZOOM = 15F

    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
}