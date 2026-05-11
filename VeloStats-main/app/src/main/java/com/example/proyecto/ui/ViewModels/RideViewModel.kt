package com.example.proyecto.ui.ViewModels

import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import java.util.concurrent.TimeUnit

class RideViewModel : ViewModel() {
    private val _rideState = MutableStateFlow(RideState())
    val rideState = _rideState.asStateFlow()

    private val _speedKmH = MutableStateFlow(0f)
    val speedKmH = _speedKmH.asStateFlow()
    private val _useLightSensor = MutableStateFlow(true)
    val useLightSensor : StateFlow<Boolean> = _useLightSensor


    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime = _elapsedTime.asStateFlow()

    private var trackingJob: Job? = null
    private var lastLocation: Location? = null
    private var totalDistance = 0f
    private var maxSpeedKmH = 0f
    private val routePoints = mutableListOf<RoutePoint>()

    fun startTracking() {
        if (_rideState.value.isTracking && !_rideState.value.isPaused) return

        routePoints.clear()
        totalDistance = 0f
        maxSpeedKmH = 0f
        _elapsedTime.value = 0L
        _speedKmH.value = 0f

        _rideState.update {
            it.copy(
                isTracking = true,
                isPaused = false,
                routePoints = emptyList(),
                distance = 0f
            )
        }

        // Iniciar actualización de tiempo
        trackingJob?.cancel()
        trackingJob = viewModelScope.launch {
            while (_rideState.value.isTracking && !_rideState.value.isPaused) {
                _elapsedTime.value += 1000L
                delay(1000L)
            }
        }
    }

    fun pauseTracking() {
        _rideState.update {
            it.copy(isPaused = true)
        }
    }

    fun stopTracking() {
        trackingJob?.cancel()
        _rideState.update {
            it.copy(
                isTracking = false,
                isPaused = false
            )
        }
    }

    fun addLocationToRoute(location: Location) {
        if (!_rideState.value.isTracking || _rideState.value.isPaused) return

        // Calcular distancia y velocidad si hay una ubicación previa
        var speedKmHForPoint = 0f
        lastLocation?.let { prevLocation ->
            val distance = prevLocation.distanceTo(location) // en metros
            totalDistance += distance

            val timeDiff = (location.time - prevLocation.time) / 1000f // en segundos
            if (timeDiff > 0) {
                val speedMS = distance / timeDiff // metros por segundo
                val speedKmH = speedMS * 3.6f // convertir a km/h
                _speedKmH.value = speedKmH
                speedKmHForPoint = speedKmH
                if (speedKmH > maxSpeedKmH) maxSpeedKmH = speedKmH
            }
        }

        lastLocation = location
        val routePoint = RoutePoint(location, location.time, speedKmHForPoint)
        routePoints.add(routePoint)

        _rideState.update { currentState ->
            currentState.copy(
                routePoints = routePoints.toList(),
                distance = totalDistance,
                currentLocation = GeoPoint(location.latitude, location.longitude)
            )
        }

        // Log para depurar
        Log.d("RideViewModel", "Ubicación añadida: Distancia=${totalDistance/1000f}km, Velocidad=${_speedKmH.value}km/h, Puntos=${routePoints.size}")
    }

    fun getDistanceKm(): Float = totalDistance / 1000f

    fun getMaxSpeedKmH(): Float = maxSpeedKmH

    fun formatElapsedTime(timeMs: Long): String {
        val hours = TimeUnit.MILLISECONDS.toHours(timeMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timeMs) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timeMs) % 60
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun saveRouteToDatabase() {
        viewModelScope.launch {
            // Implementar lógica para guardar routePoints en la base de datos
        }
    }

    override fun onCleared() {
        super.onCleared()
        trackingJob?.cancel()
    }
    fun setUseLightSensor(value: Boolean) {
        _useLightSensor.value = value
    }
}

data class RideState(
    val currentLocation: GeoPoint? = null,
    val routePoints: List<RoutePoint> = emptyList(),
    val distance: Float = 0f, // en metros
    val isTracking: Boolean = false,
    val isPaused: Boolean = false
)

data class RoutePoint(
    val location: Location,
    val timestamp: Long,
    val speedKmH: Float 
) {
    fun toGeoPoint(): GeoPoint = GeoPoint(location.latitude, location.longitude)
}
