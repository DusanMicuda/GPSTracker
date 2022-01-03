package com.micudasoftware.gpstracker.services

import android.content.Intent
import androidx.lifecycle.LifecycleService
import com.micudasoftware.gpstracker.other.Constants.ACTION_PAUSE_SERVICE
import com.micudasoftware.gpstracker.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.micudasoftware.gpstracker.other.Constants.ACTION_STOP_SERVICE


class TrackingService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.action) {
                ACTION_START_OR_RESUME_SERVICE -> {

                }
                ACTION_PAUSE_SERVICE -> {

                }
                ACTION_STOP_SERVICE -> {

                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}