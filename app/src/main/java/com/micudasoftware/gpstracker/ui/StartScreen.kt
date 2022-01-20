package com.micudasoftware.gpstracker.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.micudasoftware.gpstracker.R
import com.micudasoftware.gpstracker.db.Track
import com.micudasoftware.gpstracker.other.Event
import com.micudasoftware.gpstracker.other.Utils
import com.micudasoftware.gpstracker.ui.ui.theme.Blue
import com.micudasoftware.gpstracker.ui.ui.theme.LightBlue
import com.micudasoftware.gpstracker.ui.ui.theme.Purple700
import com.micudasoftware.gpstracker.ui.ui.theme.Shapes
import com.micudasoftware.gpstracker.ui.viewmodels.StartViewModel
import java.text.SimpleDateFormat
import java.util.*

@Preview
@Composable
fun StartScreen(
    modifier : Modifier = Modifier,
    viewModel: StartViewModel = hiltViewModel(),

) {
    val trackList = viewModel.runsSortedByDate.collectAsState(initial = emptyList())
    Scaffold(
        modifier = modifier
            .fillMaxSize(),
        backgroundColor = LightBlue,
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(24.dp),
                onClick = {
                    viewModel.onEvent(Event.OnFloatingButtonClick)
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