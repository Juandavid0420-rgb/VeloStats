package com.example.proyecto.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.R
import com.example.proyecto.Routes
import com.example.proyecto.ui.components.footerNavigation

val descripcion="Apasionado por el ciclismo, este usuario se dedica tanto al deporte como a la competencia. Su meta es mejorar su rendimiento y competir en eventos locales e internacionales. Le encanta mantener su bicicleta en óptimas condiciones y seguir las últimas tendencias en tecnología ciclista. Siempre busca superarse, ya sea entrenando solo o con otros ciclistas."
val cicla="Diseñada para velocidad y rendimiento, esta bicicleta cuenta con un cuadro ultraligero y aerodinámico, ideal para carreras y entrenamientos intensos. Equipadas con componentes de alta gama, como cambios precisos y frenos de alto rendimiento, ofrece agilidad y estabilidad en rutas exigentes. Su diseño optimizado para la eficiencia la convierte en la elección perfecta para ciclistas que buscan competir al más alto nivel."


@Composable
fun ProfileScreen(navController: NavController) {
    val textToShow= remember { mutableStateOf(descripcion) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { footerNavigation(navController = navController) }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {
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
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Text(
                text = "SpeedsterX",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.perfil2),
                contentDescription = "perfil",
                modifier = Modifier.size(280.dp)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 40.dp),
                contentScale = ContentScale.Crop
            )

            Card(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(), // Aumentar el ancho de la tarjeta para ocupar más espacio
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp), // Aumentar la elevación
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F)) // Fondo oscuro
            ) {
                Column(
                    modifier = Modifier
                        .padding(25.dp) // Añadir padding adicional dentro de la tarjeta
                ) {
                    // Botones


                    Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los botones y el texto

                    Text(
                        text = "Merida-Speder",
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 25.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp) // Añadir espacio entre el título y la descripción
                    )

                    Text(
                        text = textToShow.value,
                        style = TextStyle(
                            color = Color.Gray,
                            fontSize = 16.sp
                        ),
                        modifier = Modifier.padding(top = 8.dp) // Añadir espacio al texto para no pegarse al borde
                    )
                    Spacer(modifier = Modifier.height(24.dp)) // Espacio entre el texto y los botones

                    Button(
                        onClick = { navController.navigate(Routes.TripsScreen.route) },
                        shape = RoundedCornerShape(7.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text("Ver viajes guardados")
                    }

                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { textToShow.value= descripcion },
                    modifier = Modifier.padding(end = 8.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Descripción")
                }
                Button(
                    onClick = { textToShow.value= cicla },
                    modifier = Modifier.padding(start = 8.dp),
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.DarkGray,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Modelo de cicla")
                }
            }



        }
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview(){
    ProfileScreen(navController = rememberNavController())
}