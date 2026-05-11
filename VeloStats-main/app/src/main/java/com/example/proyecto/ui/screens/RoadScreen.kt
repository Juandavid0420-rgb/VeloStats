package com.example.proyecto.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.ui.ViewModels.RideViewModel
import com.example.proyecto.ui.components.MapsView
import com.example.proyecto.ui.components.footerNavigation

@Composable
fun RoadScreen(navController: NavController) {
    val rideViewModel: RideViewModel = viewModel()
    val rideState by rideViewModel.rideState.collectAsState()
    val currentSpeed by rideViewModel.speedKmH.collectAsState()
    val elapsedTime by rideViewModel.elapsedTime.collectAsState()
    val context = LocalContext.current

    // Manejo de permisos de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            // Permisos concedidos, continuar
        } else {
            // Manejar caso de permisos denegados
        }
    }

    // Verificar permisos al iniciar
    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (permissions.any {
                ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
            }) {
            permissionLauncher.launch(permissions)
        }
    }

    val gradientBackground = Brush.linearGradient(
        colors = listOf(Color.Black, Color(0xFF957A2B)),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { footerNavigation(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(gradientBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mi Ruta",
                fontSize = 22.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color(0xFF1F1F1F), shape = RoundedCornerShape(16.dp))
            ) {
                MapsView(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(16.dp)),
                    viewModel = rideViewModel
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonGold(
                    text = when {
                        rideState.isPaused -> "Reanudar"
                        !rideState.isTracking -> "Iniciar"
                        else -> "Pausar"
                    },
                    onClick = {
                        when {
                            !rideState.isTracking -> rideViewModel.startTracking()
                            rideState.isPaused -> rideViewModel.startTracking()
                            else -> rideViewModel.pauseTracking()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )

                ButtonGold(
                    text = "Detener",
                    onClick = { rideViewModel.stopTracking() },
                    modifier = Modifier.weight(1f),
                    enabled = rideState.isTracking
                )
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33000000), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatItem(
                    label = "Distancia",
                    value = "${"%.2f".format(rideViewModel.getDistanceKm())} km",
                    color = Color(0xFF4CAF50)
                )
                StatItem(
                    label = "Tiempo",
                    value = rideViewModel.formatElapsedTime(elapsedTime),
                    color = Color(0xFF2196F3)
                )
                StatItem(
                    label = "Velocidad",
                    value = "${"%.1f".format(currentSpeed)} km/h",
                    color = Color(0xFFFFC107)
                )
                StatItem(
                    label = "Velocidad Máx",
                    value = "${"%.1f".format(rideViewModel.getMaxSpeedKmH())} km/h",
                    color = Color(0xFFF44336)
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonGold(
                    text = "Guardar",
                    onClick = { rideViewModel.saveRouteToDatabase() },
                    modifier = Modifier.weight(1f),
                    enabled = rideState.routePoints.isNotEmpty() && !rideState.isTracking
                )
                ButtonGold(
                    text = "Compartir",
                    onClick = { /* TODO: Implementar lógica para compartir */ },
                    modifier = Modifier.weight(1f),
                    enabled = rideState.routePoints.isNotEmpty() && !rideState.isTracking
                )
            }
        }
    }
}

@Composable
fun ButtonGold(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFFC107),
            contentColor = Color.Black,
            disabledContainerColor = Color(0x66FFC107)
        ),
        enabled = enabled,
        modifier = modifier
    ) {
        Text(text)
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.White.copy(alpha = 0.8f))
        Text(value, color = color, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun RoadScreenPreview() {
    RoadScreen(navController = rememberNavController())
}