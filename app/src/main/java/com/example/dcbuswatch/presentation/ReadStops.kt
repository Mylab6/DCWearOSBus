package com.example.dcbuswatch.presentation

import android.content.Context
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class StopWithDistance(val stopId: String, val stopCode: String ,val stopName: String, val lat: Double, val lon: Double, var distanceMeters: Double)

class ReadStops(private val context: Context) {
    private val stops: List<StopWithDistance>

    init {
        stops = loadStops()
    }

    private fun loadStops(): List<StopWithDistance> {
        val assetManager = context.assets
        val file = assetManager.open("stops.txt")
        val stopsData = csvReader().readAllWithHeader(file)

        return stopsData.map { row ->
            StopWithDistance(
                row["stop_id"] ?: "",
                row["stop_code"] ?: "",
                row["stop_name"] ?: "",
                row["stop_lat"]?.toDoubleOrNull() ?: 0.0,
                row["stop_lon"]?.toDoubleOrNull() ?: 0.0,

                0.0 // Initialize distance to 0 (will be calculated later)
            )
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * asin(sqrt(a))
        return 6371000 * c // Radius of the Earth in meters
    }

    fun findStopsInRadius(lat: Double, lon: Double, radius: Double): List<StopWithDistance> {
        val matchingStops = mutableListOf<StopWithDistance>()

        for (stop in stops) {
            val distance = haversine(lat, lon, stop.lat, stop.lon)
            if (distance <= radius) {
                stop.distanceMeters = distance // Set the calculated distance
                matchingStops.add(stop)
            }
        }

        // Sort the matching stops by distance, closest to furthest
        matchingStops.sortBy { it.distanceMeters }

        return matchingStops
    }
}
