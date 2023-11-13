package com.example.dcbuswatch.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material.Card
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.wear.compose.material.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
//import com.google.android.material.floatingactionbutton.FloatingActionButton

@Composable
fun ClosestStopsScreen(closestStops: List<StopWithDistance> , onRefresh: () -> Unit) {
    CustomWearMaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            val selectedStop = remember { mutableStateOf<StopWithDistance?>(null) }
            if (selectedStop.value == null) {
                ClosestStopsList(stops = closestStops) { clickedStop ->
                    selectedStop.value = clickedStop
                }
            } else {
                StopDetail(stopCode = selectedStop.value!!.stopCode) {
                    selectedStop.value = null
                }
            }
            FloatingActionButton(
                onClick = onRefresh,
                modifier = Modifier.align(Alignment.BottomCenter) // Position the FAB at the bottom center
            ) {
                // Replace with an appropriate icon
                Text("Refresh")
            }

        }
    }
}

@Composable
fun ClosestStopsList(stops: List<StopWithDistance>, onItemClick: (StopWithDistance) -> Unit) {
    val scrollState = rememberLazyListState()

    LazyColumn(
        state = scrollState,
        modifier = Modifier.padding(16.dp) // Add padding for circular layout
    ) {
        items(stops) { stop ->
            StopItem(stop, onItemClick)
        }
    }
}


@Composable
fun StopItem(stop: StopWithDistance, onItemClick: (StopWithDistance) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onItemClick(stop) },
        shape = RectangleShape, // Square corners
        backgroundColor = MaterialTheme.colors.primary // Ensure there's a contrast with the text color
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = stop.stopName,
                style = MaterialTheme.typography.title2,
                color = Color.White, // Set a text color that contrasts with the card's background
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stop.stopCode,
                style = MaterialTheme.typography.title3,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Distance: ${stop.distanceMeters.toInt()} meters",
                style = MaterialTheme.typography.body1,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun CustomWearMaterialTheme(content: @Composable () -> Unit) {
    val customColors = MaterialTheme.colors.copy(
        primary = Color(0xFF1E88E5), // A shade of blue
        onPrimary = Color.White, // White text on primary color
        // Define other colors as needed
        // ...
    )

    MaterialTheme(
        colors = customColors,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
