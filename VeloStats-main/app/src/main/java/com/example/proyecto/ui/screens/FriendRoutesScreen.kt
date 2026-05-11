package com.example.proyecto.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto.data.models.Trip
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendRoutesScreen(userId: String, navController: NavController) {
    var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        try {
            val result = FirebaseFirestore.getInstance()
                .collection("trips")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val tripsList = result.documents.mapNotNull { it.toObject(Trip::class.java) }
            Log.d("FriendRoutesScreen", "Obtuvo ${tripsList.size} trips para $userId")

            trips = tripsList
        } catch (e: Exception) {
            Log.e("FriendRoutesScreen", "Error al obtener trips: ${e.message}")
        } finally {
            isLoading = false
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rutas de $userId") })
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(trips.size) { index ->
                    val trip = trips[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        elevation = CardDefaults.cardElevation()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = trip.title, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Distancia: ${trip.distanceKm} km")
                            Text(text = "Velocidad media: ${trip.avgSpeed} km/h")
                        }
                    }
                }
            }
        }
    }
}