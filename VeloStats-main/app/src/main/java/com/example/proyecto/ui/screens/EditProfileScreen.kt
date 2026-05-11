package com.example.proyecto.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyecto.ui.theme.cardBackground
import com.example.proyecto.ui.theme.gradientEnd
import com.example.proyecto.ui.components.footerNavigation
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person

@Composable
fun EditProfileScreen(navController: NavController) {
    // Estados para los campos editables
    var name by remember { mutableStateOf("Usuario") }
    var email by remember { mutableStateOf("usuario@ejemplo.com") }
    var modeloCicla by remember { mutableStateOf("Cicla todo terreno") }
    var descripcionCicla by remember { mutableStateOf("Cicla todo terreno apta para cualquier tipo de ambiente y de alta intensidad...") }
    var descripcionUsusaio by remember { mutableStateOf("Soy fulanito y soy un experto en el ciclo montañismo...") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { footerNavigation(navController = navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color.Black, gradientEnd),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    )
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "Editar Perfil",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineLarge
                    )
                }

                item {
                    // Foto de perfil
                    ProfilePictureCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(8.dp)
                    )
                }

                item {
                    // Campo de Nombre
                    ProfileTextFieldCard(
                        label = "Nombre",
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(8.dp)
                    )
                }

                item {
                    // Campo de Correo Electrónico
                    ProfileTextFieldCard(
                        label = "Correo Electrónico",
                        value = email,
                        onValueChange = { email = it },
                        keyboardType = KeyboardType.Email,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(8.dp)
                    )
                }

                item {
                    // Campo de Modelo de Cicla
                    ProfileTextFieldCard(
                        label = "Modelo de cicla",
                        value = modeloCicla,
                        onValueChange = { modeloCicla = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(8.dp)
                    )
                }

                item {
                    // Campo de Descripción de la Cicla
                    ProfileTextFieldCard(
                        label = "Descripción de la cicla",
                        value = descripcionCicla,
                        onValueChange = { descripcionCicla = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        isMultiline = true
                    )
                }

                item {
                    // Campo de Descripción de Usuario
                    ProfileTextFieldCard(
                        label = "Descripción de usuario",
                        value = descripcionUsusaio,
                        onValueChange = { descripcionUsusaio = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        isMultiline = true
                    )
                }

                item {
                    // Botón de Guardar
                    SaveButtonCard(
                        onClick = {
                            // TODO: Implementar lógica para guardar los cambios
                            println("Perfil actualizado: Nombre=$name, Email=$email, Modelo=$modeloCicla")
                            navController.popBackStack() // Regresar a SettingsScreen
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ProfilePictureCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(40.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Cambiar Foto",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable {
                        // TODO: Implementar selección de foto (galería/cámara)
                        println("Abrir selector de foto")
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileTextFieldCard(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isMultiline: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text(label, color = Color.White) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = gradientEnd,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                    textStyle = MaterialTheme.typography.bodyLarge,
                    minLines = if (isMultiline) 3 else 1, // Mínimo 3 líneas para campos multilínea
                    maxLines = if (isMultiline) 5 else 1 // Máximo 5 líneas para campos multilínea
                )
            }
        }
    }
}

@Composable
fun SaveButtonCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = gradientEnd)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Guardar Cambios",
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}