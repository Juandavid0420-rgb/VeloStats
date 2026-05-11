package com.example.proyecto.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.R
import com.example.proyecto.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseDatabase.getInstance().reference

    // Verificar si el usuario ya está autenticado
    LaunchedEffect(auth.currentUser) {
        if (auth.currentUser != null) {
            println("User already authenticated: ${auth.currentUser!!.uid}")
            navController.navigate(Routes.MainScreen.route) {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
        }
    }

    // Estados para los campos de texto
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Configuración para Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(context, gso)

    // Launcher para Google Sign-In
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        isLoading = true
        println("Google Sign-In result: ${result.resultCode}, data: ${result.data}")
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account = task.getResult(ApiException::class.java)
                    val idToken = account.idToken
                    println("Google Sign-In account: ${account.email}, idToken: ${idToken != null}")
                    if (idToken != null) {
                        val credential = GoogleAuthProvider.getCredential(idToken, null)
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { authTask ->
                                isLoading = false
                                if (authTask.isSuccessful) {
                                    println("Google login successful: ${auth.currentUser!!.uid}")
                                    navController.navigate(Routes.MainScreen.route) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                } else {
                                    errorMessage = authTask.exception?.message ?: "Error al iniciar sesión con Google"
                                    println("Google login failed: $errorMessage")
                                }
                            }
                    } else {
                        isLoading = false
                        errorMessage = "No se obtuvo el ID token de Google"
                        println("Google Sign-In failed: No ID token")
                    }
                } catch (e: ApiException) {
                    isLoading = false
                    errorMessage = "Google Sign-In failed: ${e.statusCode} - ${CommonStatusCodes.getStatusCodeString(e.statusCode)}"
                    println("Google Sign-In exception: ${e.statusCode} - ${e.message}")
                } catch (e: Exception) {
                    isLoading = false
                    errorMessage = "Error inesperado: ${e.message}"
                    println("Unexpected error: ${e.message}")
                }
            }
            Activity.RESULT_CANCELED -> {
                isLoading = false
                errorMessage = "Google Sign-In cancelado por el usuario"
                println("Google Sign-In canceled by user")
            }
            else -> {
                isLoading = false
                errorMessage = "Google Sign-In fallido: resultCode ${result.resultCode}. Verifica SHA-1 y default_web_client_id."
                println("Google Sign-In result not OK: ${result.resultCode}, intent: ${result.data?.toString()}")
            }
        }
    }

    // Fondo degradado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color.Black, Color(0xFFB27800)),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(1000f, 1000f)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título principal
            Text(
                text = "Inicio de sesión",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Tarjeta informativa
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.8f))
            ) {
                Text(
                    text = "¡Una nueva forma de hacer tus viajes y crear nuevas experiencias en tus aventuras!",
                    fontSize = 16.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mensaje de error
            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Campo usuario
            TextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo contraseña
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de login
            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        println("Attempting email login: $username")
                        auth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) {
                                    println("Email login successful: ${auth.currentUser!!.uid}")
                                    navController.navigate(Routes.MainScreen.route) {
                                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                                    }
                                } else {
                                    val exception = task.exception
                                    errorMessage = when {
                                        exception?.message?.contains("no user record") == true ->
                                            "Usuario no registrado. Por favor, regístrate primero."
                                        exception?.message?.contains("password is invalid") == true ->
                                            "Contraseña incorrecta. Intenta de nuevo."
                                        else -> exception?.message ?: "Error al iniciar sesión"
                                    }
                                    println("Email login failed: $errorMessage")
                                }
                            }
                    } else {
                        errorMessage = "Por favor, completa todos los campos"
                        println("Email login failed: Empty fields")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDAA520)),
                enabled = !isLoading
            ) {
                Text(
                    text = "¡Vamos!!",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Google Sign-In
            Button(
                onClick = {
                    isLoading = true
                    println("Starting Google Sign-In")
                    val signInIntent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = "Google Icon",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Iniciar con Google",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Enlace de registro
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "¿No tienes una cuenta? ",
                    color = Color.White,
                    fontSize = 14.sp
                )
                Text(
                    text = "Crear cuenta",
                    color = Color(0xFFB27800),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate(Routes.CreateProfileScreen.route)
                    }
                )
            }

            // Indicador de carga
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}