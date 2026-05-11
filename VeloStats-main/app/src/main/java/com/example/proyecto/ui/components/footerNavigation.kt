package com.example.proyecto.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.Routes
import com.example.proyecto.ui.screens.ProfileScreen


@Composable
fun footerNavigation(navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.navigate(Routes.MainScreen.route) }) {
            Icon(
                imageVector = Icons.Filled.Home,
                contentDescription = "Home",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }

        IconButton(onClick = { navController.navigate(Routes.RoadScreen.route) }) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Ruta",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }

        IconButton(onClick = {

            try {
                navController.navigate(Routes.ProfileScreen.route)
            } catch (e: Exception) {
                Log.e("NavigationError", "Error al navegar a ProfileScreen", e)
            }

        }) {
            Icon(
                imageVector = Icons.Filled.AccountCircle,
                contentDescription = "Perfil",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }

        IconButton(onClick = { navController.navigate(Routes.WorkoutsScreen.route) }) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "Entrenamientos",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }

        IconButton(onClick = { navController.navigate(Routes.StartScreen.route) }) {
            Icon(
                imageVector = Icons.Filled.ExitToApp,
                contentDescription = "Salir",
                modifier = Modifier.size(30.dp),
                tint = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FooterScreenPreview(){
    footerNavigation(navController = rememberNavController())
}
