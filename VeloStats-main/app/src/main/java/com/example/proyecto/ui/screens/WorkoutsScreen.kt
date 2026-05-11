package com.example.proyecto.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.R
import com.example.proyecto.ui.components.footerNavigation

@Composable
fun WorkoutsScreen(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { footerNavigation(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color.Black, Color(0xFF957A2B))
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Mis entrenamientos anteriores",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Lista de rutas
            RutaCard(
                titulo = "Domingo solitario hasta sopó",
                fechaHora = "15 de septiembre 2024–6:00AM",
                imagen = painterResource(id = R.drawable.ruta1),
                colorTitulo = Color(0xFFFFD700)
            )

            RutaCard(
                titulo = "Salida al parque",
                fechaHora = "20 de Enero 2025–7:00AM",
                imagen = painterResource(id = R.drawable.ruta2),
                colorTitulo = Color(0xFFFFB300)
            )

            RutaCard(
                titulo = "Viernes montañero con amigos",
                fechaHora = "9 de marzo 2025–10:00AM",
                imagen = painterResource(id = R.drawable.ruta3),
                colorTitulo = Color(0xFFFF5722)
            )
        }
    }
}

@Composable
fun RutaCard(
    titulo: String,
    fechaHora: String,
    imagen: Painter,
    colorTitulo: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1F1F1F))
        ) {
            // Título encima de la imagen
            Text(
                text = titulo,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colorTitulo,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )

            // Imagen
            Image(
                painter = imagen,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .graphicsLayer(alpha = 1f),
                contentScale = ContentScale.Crop
            )

            // Fecha y hora
            Text(
                text = fechaHora,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            )
        }
    }
}
