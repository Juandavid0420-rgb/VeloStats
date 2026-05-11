package com.example.proyecto.viewmodels

import com.example.proyecto.data.models.Trip
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.example.proyecto.repository.TripRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TripViewModel : ViewModel() {
    private val repository = TripRepository()
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> get() = _trips

    fun loadTrips(userId: String) {
        viewModelScope.launch {
            _trips.value = repository.getTripsByUser(userId)
        }
    }

    fun deleteTrip(userId: String, tripId: String) {
        viewModelScope.launch {
            repository.deleteTrip(userId, tripId)
            loadTrips(userId)
        }
    }


    fun highlightTrip(tripId: String, userId: String) {
        viewModelScope.launch {
            repository.setHighlightedTrip(tripId, userId)
            loadTrips(userId)
        }
    }

    fun saveTrip(trip: Trip) {
        viewModelScope.launch {
            repository.saveTrip(trip)
            loadTrips(trip.userId)
        }
    }
}
