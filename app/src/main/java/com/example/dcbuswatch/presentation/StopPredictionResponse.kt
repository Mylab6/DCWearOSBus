package com.example.dcbuswatch.presentation

import kotlinx.serialization.Serializable

@Serializable
data class StopPredictionResponse(
    val StopName: String,
    val Predictions: List<Prediction>
)

@Serializable
data class Prediction(
    val RouteID: String,
    val DirectionText: String,
    val DirectionNum: String,
    val Minutes: Int,
    val VehicleID: String,
    val TripID: String
)
