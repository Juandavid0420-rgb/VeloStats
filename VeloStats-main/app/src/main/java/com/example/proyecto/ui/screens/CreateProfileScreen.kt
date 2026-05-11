package com.example.proyecto.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.R
import com.example.proyecto.Routes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@Composable
fun CreateProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseDatabase.getInstance().reference
    val coroutineScope = rememberCoroutineScope()

    // Estados para los campos y UI
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF121212), Color(0xFFB27800)),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            )
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Título superior
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Creación de cuenta",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.perfil),
                contentDescription = "Perfil",
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta de bienvenida
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xCC2B2B2B)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Text(
                text = "Crea tu nueva cuenta y\ncomencemos!",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Mensaje de error
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }

        // Formulario
        AuthTextField(value = name, label = "Nombre", onValueChange = { name = it })
        AuthTextField(value = email, label = "Correo", onValueChange = { email = it })
        AuthTextField(value = password, label = "Contraseña", onValueChange = { password = it }, isPassword = true)
        AuthTextField(value = confirmPassword, label = "Confirma tu contraseña", onValueChange = { confirmPassword = it }, isPassword = true)

        Spacer(modifier = Modifier.height(24.dp))

        // Botón crear cuenta
        Button(
            onClick = {
                // Validar campos
                when {
                    name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                        errorMessage = "Por favor, completa todos los campos"
                    }
                    password != confirmPassword -> {
                        errorMessage = "Las contraseñas no coinciden"
                    }
                    password.length < 6 -> {
                        errorMessage = "La contraseña debe tener al menos 6 caracteres"
                    }
                    !email.contains("@") || !email.contains(".") -> {
                        errorMessage = "Ingresa un correo válido"
                    }
                    else -> {
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                // Registrar usuario en Firebase Authentication
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            // Subir datos a Realtime Database
                                            val userId = auth.currentUser?.uid
                                            if (userId != null) {
                                                val userData = mapOf(
                                                    "name" to name,
                                                    "email" to email
                                                )
                                                db.child("users").child(userId).setValue(userData)
                                                    .addOnCompleteListener { dbTask ->
                                                        if (dbTask.isSuccessful) {
                                                            // Navegar a main_screen
                                                            navController.navigate(Routes.MainScreen.route) {
                                                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                                            }
                                                        } else {
                                                            errorMessage = "Error al guardar datos: ${dbTask.exception?.message}"
                                                        }
                                                    }
                                            } else {
                                                errorMessage = "Error: No se pudo obtener el ID del usuario"
                                            }
                                        } else {
                                            errorMessage = when {
                                                task.exception?.message?.contains("email address is already in use") == true ->
                                                    "El correo ya está registrado"
                                                task.exception?.message?.contains("invalid email") == true ->
                                                    "Correo inválido"
                                                else -> task.exception?.message ?: "Error al registrar"
                                            }
                                        }
                                    }
                            } catch (e: Exception) {
                                isLoading = false
                                errorMessage = e.message
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xCC1C1C1C)),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading
        ) {
            Text(text = "Crear cuenta", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ya tengo una cuenta
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ya tengo una cuenta ",
                color = Color.White,
                fontSize = 14.sp
            )
            TextButton(
                onClick = { navController.navigate(Routes.StartScreen.route) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Iniciar sesión",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }

        // Indicador de carga
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = Color.White)
        }
    }
}

@Composable
fun AuthTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Color.White) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedBorderColor = Color(0xFFB27800),
            unfocusedBorderColor = Color.White,
            cursorColor = Color.White,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = Color.White,
            unfocusedLabelColor = Color.Gray
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None
    )
}