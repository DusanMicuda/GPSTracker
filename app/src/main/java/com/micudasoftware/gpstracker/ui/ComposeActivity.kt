package com.micudasoftware.gpstracker.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.micudasoftware.gpstracker.ui.screens.NavGraphs
import com.micudasoftware.gpstracker.ui.theme.GPSTrackerTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPermissionsApi
@AndroidEntryPoint
class ComposeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WindowCompat.setDecorFitsSystemWindows(window, false)
                GPSTrackerTheme {
                    Surface(color = MaterialTheme.colors.background) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root
                        )
                    }
                }
        }
    }
}