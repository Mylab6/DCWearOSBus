/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package com.example.dcbuswatch.presentation
import android.Manifest

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import com.example.dcbuswatch.R
import com.example.dcbuswatch.presentation.theme.DCBusWatchTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        requestLocationPermission()

        setContent {
            DCBusWatchTheme {
                val list = remember { mutableStateOf<List<StopWithDistance>>(listOf()) }
                val locationFetchSuccessful = remember { mutableStateOf(true) }
                var failureReason = remember { mutableStateOf("") }

                // Function to refresh location and list
                val refreshLocationAndList = {
                    getCurrentLocation(
                        onSuccess = { lat, lon ->
                            val testRadius = 500.00
                            list.value = findStopsInRadius(lat, lon, testRadius)
                            locationFetchSuccessful.value = true
                        },
                        onFailure = { failureText ->
                            failureReason.value = failureText
                            locationFetchSuccessful.value = false
                        }
                    )
                }

                LaunchedEffect(Unit) {
                    refreshLocationAndList()
                }

                if (locationFetchSuccessful.value) {
                    ClosestStopsScreen(list.value, refreshLocationAndList)
                } else {
                    ErrorScreen(failureReason.value)
                }
            }
        }
    }
    private fun findStopsInRadius(userLat: Double, userLon: Double, searchRadius: Double): List<StopWithDistance> {
        // Obtain the application's context
        val context: Context = this

        // Create an instance of the ReadStops class
        val readStops = ReadStops(context)

        // Find stops within the specified radius
        val matchingStops = readStops.findStopsInRadius(userLat, userLon, searchRadius)

        // Process the matching stops as needed
        for (stop in matchingStops) {
            // Perform actions with the matching stops
            println("Matching Stop - ID: ${stop.stopId}, Name: ${stop.stopName}")
        }
        return matchingStops
    }

    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1)
        }
    }


    private fun getCurrentLocation(onSuccess: (Double, Double) -> Unit, onFailure: (String) -> Unit) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {

            val locationRequest = LocationRequest.create().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                numUpdates = 1 // Request a single update
            }

            val locationCallback = object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    fusedLocationClient.removeLocationUpdates(this) // Remove updates immediately
                    p0 ?: return onFailure("Location result is null")
                    val location = p0.lastLocation
                    if (location != null) {
                        onSuccess(location.latitude, location.longitude)
                    } else {
                        onFailure("Location is null")
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            onFailure("Location permission not granted")
        }
    }
    @Composable
    fun ErrorScreen(errorMessage: String) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.body1,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}





