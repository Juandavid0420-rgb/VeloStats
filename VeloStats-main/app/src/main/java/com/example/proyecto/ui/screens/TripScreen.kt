package com.example.proyecto.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyecto.data.models.Trip
import com.example.proyecto.viewmodels.TripViewModel
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*




@Composable
fun TripsScreen(
    userId: String,
    viewModel: TripViewModel = viewModel()
) {
    LaunchedEffect(userId) {
        viewModel.loadTrips(userId)
    }

    val trips by viewModel.trips.collectAsState()

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Fondo degradado negro - dorado
            Canvas(modifier = Modifier.fillMaxSize()) {
                val width = size.width
                val height = size.height

                drawRect(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Black, Color(0xFF957A2B)),
                        start = Offset(width, 0f),
                        end = Offset(0f, height)
                    ),
                    size = size
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Tus viajes",
                    color = Color(0xFFFFD700),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn {
                    items(trips) { trip ->
                        TripItem(
                            trip = trip,
                            onDelete = { viewModel.deleteTrip(trip.userId, trip.id) },
                            onHighlight = { viewModel.highlightTrip(trip.id, trip.userId) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun TripItem(
    trip: Trip,
    onDelete: () -> Unit,
    onHighlight: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = trip.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge
                )
                if (trip.isHighlighted) {
                    Text("⭐", fontSize = MaterialTheme.typography.headlineSmall.fontSize)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Fecha: ${trip.date.formatAsDate()}", color = Color.Gray)
            Text("Distancia: ${trip.distanceKm} km", color = Color.Gray)
            Text("Velocidad media: ${trip.avgSpeed} km/h", color = Color.Gray)
            Text("Velocidad máxima: ${trip.maxSpeed} km/h", color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onHighlight,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD700))
                ) {
                    Text("Destacar", color = Color.Black)
                }
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            }
        }
    }
}

fun Timestamp.formatAsDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(this.toDate()) // Convierte Timestamp a Date
}
fun Long.formatAsDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}