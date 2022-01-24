package com.micudasoftware.gpstracker.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.micudasoftware.gpstracker.other.Event
import com.micudasoftware.gpstracker.services.TrackingService
import com.micudasoftware.gpstracker.ui.destinations.StartScreenDestination
import com.micudasoftware.gpstracker.ui.viewmodels.TrackingViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect

@ExperimentalPermissionsApi
@Destination
@Composable
fun TrackScreen(
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    val rememberMapView = rememberMapViewWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.eventChannel, block = {
        viewModel.eventChannel.collect { event ->
            when (event) {
                is Event.Navigate -> {
                    navigator.navigate(StartScreenDestination)
                }
                is Event.SendCommandToService -> {
                    Intent(context, TrackingService::class.java).also {
                        it.action = event.command
                        context.startService(it)
                    }
                }
                is Event.ShowToast -> {
                    scaffoldState.snackbarHostState.showSnackbar(event.message, null, SnackbarDuration.Short)
                }
                else -> Unit
            }
        }
    })

    Scaffold(scaffoldState = scaffoldState) {
        Box(modifier = modifier) {
            AndroidView( {rememberMapView} ) { mapView ->
                mapView.getMapAsync {
                    viewModel.map = it
                    viewModel.addAllPolylines()
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp)
        ) {
            val btnText = viewModel.btnText.collectAsState()
            Button(
                shape = RoundedCornerShape(20.dp),
                onClick = {
                    viewModel.setMapViewSize(rememberMapView.width, rememberMapView.height)
                    viewModel.toggleTrack()
                }
            ) {
                Text(text = btnText.value)
            }
        }
    }
}
