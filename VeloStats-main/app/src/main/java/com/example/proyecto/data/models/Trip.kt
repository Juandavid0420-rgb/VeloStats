package com.example.proyecto.data.models
import com.google.firebase.Timestamp

data class LatLngPoint(
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val speed: Float = 0f
)

data class Trip(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val date: Timestamp = Timestamp.now(),
    val distanceKm: Float = 0f,
    val avgSpeed: Float = 0f,
    val maxSpeed: Float = 0f,
    val isHighlighted: Boolean = false,
    val path: List<LatLngPoint> = emptyList()
)


