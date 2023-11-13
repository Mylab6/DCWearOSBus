package com.example.dcbuswatch.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.dcbuswatch.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

// Define your data classes

@Composable
fun StopDetail(stopCode: String, onBackClick: () -> Unit) {
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var stopDetail by remember { mutableStateOf<StopPredictionResponse?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    print("Requesting " + stopCode)
    LaunchedEffect(stopCode) {
        coroutineScope.launch {
            try {

                // Before making public change this to an environment var
                // And delete all git history
                val apiUrl = "https://" + BuildConfig.API_BASE_URL + "/stop?StopID=$stopCode"
                val response: HttpResponse = HttpClient(CIO).get(apiUrl)
                val responseBody = response.bodyAsText()
                val details = Json.decodeFromString<StopPredictionResponse>(responseBody)

                if (details.Predictions.isEmpty()) {
                    errorMessage = "No predictions found for this stop."
                } else {
                    stopDetail = details
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "Failed to load data: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    when {
        isLoading -> CircularProgressIndicator()
        errorMessage != null -> Text(errorMessage!!, style = MaterialTheme.typography.body1)
        else -> stopDetail?.let { detailedStop ->
            Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {
                Text(text = "Stop Name: ${detailedStop.StopName}", style = MaterialTheme.typography.title3)
                Text(text = "Direction: ${detailedStop.Predictions[0].DirectionText}", style = MaterialTheme.typography.title3)

                detailedStop.Predictions.forEach { prediction ->
                    Text(text = "Route: ${prediction.RouteID}, Minutes: ${prediction.Minutes}", style = MaterialTheme.typography.body1)
                }
                Button(onClick = { onBackClick() }) {
                    Text("Back")
                }
            }
        }
    }
}
