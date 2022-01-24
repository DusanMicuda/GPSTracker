package com.micudasoftware.gpstracker.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.other.Event
import com.micudasoftware.gpstracker.other.Utils
import com.micudasoftware.gpstracker.ui.ui.theme.Blue
import com.micudasoftware.gpstracker.ui.ui.theme.LightBlue
import com.micudasoftware.gpstracker.ui.viewmodels.StartViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.content.Intent
import android.net.Uri
import android.provider.Settings


@ExperimentalPermissionsApi
@Composable
fun StartScreen(
    modifier : Modifier = Modifier,
    viewModel: StartViewModel = hiltViewModel()
) {
    val trackList = viewModel.runsSortedByDate.collectAsState(initial = emptyList())
    val locationPermissionsState =
        rememberMultiplePermissionsState(
            permissions =
                listOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
        )
    val backgroundLocationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    val openDialog = remember {mutableStateOf(false)}
    val context = LocalContext.current
//
//    val lifecycleOwner = LocalLifecycleOwner.current
//    DisposableEffect(
//        key1 = lifecycleOwner,
//        effect = {
//            val observer = LifecycleEventObserver { _, event ->
//                if (event == Lifecycle.Event.ON_RESUME)
//                    locationPermissionsState.launchMultiplePermissionRequest()
//            }
//            lifecycleOwner.lifecycle.addObserver(observer)
//
//            onDispose {
//                lifecycleOwner.lifecycle.removeObserver(observer)
//            }
//        })

    if (openDialog.value) {
        AlertDialog(
            title = { Text(text = "Permissions Denied!") },
            text = { Text(text = "You need to accept location permissions to use this app.")},
            onDismissRequest = {
                openDialog.value = false
            },
            dismissButton = {
                OutlinedButton(
                    border = BorderStroke(1.dp, Color.Blue),
                    shape = RoundedCornerShape(20.dp),
                    onClick = { openDialog.value = false }
                ) {
                    Text(text = "Cancel")
                }
            },
            confirmButton = {
                Button(
                    shape = RoundedCornerShape(20.dp),
                    onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri: Uri = Uri.fromParts("package", context.packageName, null)
                        intent.data = uri
                        context.startActivity(intent)
                        openDialog.value = false
                    }
                ) {
                    Text(text = "Ok")
                }
            }
        )
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        backgroundColor = LightBlue,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(24.dp),
                onClick = {
                    when {
                        locationPermissionsState.allPermissionsGranted -> {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                when {
                                    backgroundLocationPermissionState.hasPermission -> {
                                        viewModel.onEvent(Event.OnFloatingButtonClick)
                                    }
                                    backgroundLocationPermissionState.shouldShowRationale -> {
                                        openDialog.value = true
                                    }
                                    !backgroundLocationPermissionState.hasPermission ->
                                        backgroundLocationPermissionState.launchPermissionRequest()
                                }
                            } else
                                viewModel.onEvent(Event.OnFloatingButtonClick)
                        }
                        locationPermissionsState.shouldShowRationale -> {
                            openDialog.value = true
                        }
                        !locationPermissionsState.allPermissionsGranted -> {
                            locationPermissionsState.launchMultiplePermissionRequest()
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            modifier = Modifier.fillMaxSize()
        ) {
            AppBar()
            TrackList(trackList = trackList.value)
        }
    }
}

@Composable
fun AppBar(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth(),
            text = stringResource(id = R.string.app_name),
            fontSize = MaterialTheme.typography.h4.fontSize,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Color.White
        )
        Divider(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .align(CenterHorizontally)
            , color = Blue
        )
    }

}

@Composable
fun TrackList(
    trackList: List<Track>,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier
        .fillMaxWidth()
        .wrapContentHeight()) {
        items(trackList) { track ->
            TrackItem(track = track)
        }
    }
}

@Composable
fun TrackItem(
    track: Track,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)) {
        Card(
            shape = RoundedCornerShape(10),
            modifier = modifier
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth(),
                    bitmap = track.image.let { it!!.asImageBitmap() },
                    contentDescription = "Track Image",
                    contentScale = ContentScale.FillBounds
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(8.dp)
                ) {
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = track.dateInMillis
                    }
                    val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                    Text(text = dateFormat.format(calendar.time))
                    Text(text = Utils.getFormattedTime(track.timeInMillis))
                    Text(text = "${track.distanceInMeters}km")
                    Text(text = "${track.avgSpeedInKMH}km/h")
                }
            }
        }
    }
}