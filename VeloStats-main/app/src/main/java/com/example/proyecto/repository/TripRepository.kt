package com.example.proyecto.repository

import com.example.proyecto.data.models.Trip
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class TripRepository {
    private val db = FirebaseDatabase.getInstance()
    private val tripsRef = db.getReference("trips")

    suspend fun saveTrip(trip: Trip) {
        tripsRef.child(trip.userId).child(trip.id).setValue(trip).await()
    }

    suspend fun deleteTrip(userId: String, tripId: String) {
        tripsRef.child(userId).child(tripId).removeValue().await()
    }

    suspend fun getTripsByUser(userId: String): List<Trip> {
        val snapshot = tripsRef.child(userId).get().await()
        return snapshot.children.mapNotNull { it.getValue(Trip::class.java) }
    }

    suspend fun setHighlightedTrip(userId: String, tripId: String) {
        val userTripsRef = tripsRef.child(userId)
        val snapshot = userTripsRef.get().await()
        for (child in snapshot.children) {
            userTripsRef.child(child.key!!).child("isHighlighted").setValue(child.key == tripId).await()
        }
    }
}
