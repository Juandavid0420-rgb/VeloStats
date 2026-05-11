package com.example.proyecto.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyecto.R
import com.example.proyecto.Routes
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Configuración para Google Sign-In
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)

    // Función para cerrar sesión
    fun signOut() {
        coroutineScope.launch {
            try {
                // Cerrar sesión en Firebase
                FirebaseAuth.getInstance().signOut()
                // Cerrar sesión en Google
                googleSignInClient.signOut().addOnCompleteListener {
                    println("Google Sign-Out completed")
                }
                // Navegar a LoginScreen
                navController.navigate(Routes.LoginScreen.route) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = true }
                }
                println("Firebase Sign-Out completed")
            } catch (e: Exception) {
                println("Sign-Out failed: ${e.message}")
            }
        }
    }

    // Lista de secciones con sus rutas de navegación
    val sections = listOf(
        Section("INICIAR ENTRENO", R.drawable.ciclista_icon, Routes.RoadScreen.route),
        Section("Entrenos", R.drawable.clock_icon, Routes.WorkoutsScreen.route),
        Section("Ajustes", R.drawable.settings_icon, Routes.SettingScreen.route),
        Section("Amigos", R.drawable.friends_icon, Routes.FriendScreen.route),
        Section("Tu equipo", R.drawable.equipment_icon, Routes.ProfileScreen.route)
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF121212), Color(0xFFB27800)),
                        start = Offset(0f, 0f),
                        end = Offset(1000f, 1000f)
                    )
                )
                .padding(16.dp)
        ) {
            // Título superior
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿Qué quieres hacer hoy?",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Row {
                    IconButton(
                        onClick = { navController.navigate(Routes.ProfileScreen.route) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.perfil),
                            contentDescription = "Perfil",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    IconButton(
                        onClick = { signOut() },
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ExitToApp,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.White
                        )
                    }
                }
            }

            // Secciones mejoradas
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sections) { section ->
                    SectionCard(
                        title = section.title,
                        iconRes = section.iconRes,
                        onClick = { navController.navigate(section.route) }
                    )
                }
            }
        }
    }
}

// Data class para las secciones
data class Section(
    val title: String,
    val iconRes: Int,
    val route: String
)

@Composable
fun SectionCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0x802B2B2B) // Semi-transparente
        ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x60B27800), // Dorado semi-transparente
                            Color(0x802B2B2B) // Gris oscuro semi-transparente
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier
                        .size(56.dp)
                        .graphicsLayer {
                            // Efecto de iluminación en los iconos
                            alpha = 0.9f
                        },
                    colorFilter = ColorFilter.tint(Color.White) // Iconos blancos
                )
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Efecto de brillo al hacer hover (en dispositivos con mouse)
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0f),
                color = Color.Transparent,
                content = {},
                shape = RoundedCornerShape(20.dp),
                shadowElevation = 4.dp
            )
        }
    }
}

// BottomNavigationBar mejorado
@Composable
fun BottomNavigationBar(navController: NavController) {
    val currentRoute = navController.currentDestination?.route

    NavigationBar(
        containerColor = Color(0xCC1C1C1C), // Más transparente
        tonalElevation = 12.dp
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Inicio",
                    tint = if (currentRoute == Routes.MainScreen.route) Color(0xFFB27800) else Color.White
                )
            },
            selected = currentRoute == Routes.MainScreen.route,
            onClick = { navController.navigate(Routes.MainScreen.route) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Entrenos",
                    tint = if (currentRoute == Routes.WorkoutsScreen.route) Color(0xFFB27800) else Color.White
                )
            },
            selected = currentRoute == Routes.WorkoutsScreen.route,
            onClick = { navController.navigate(Routes.WorkoutsScreen.route) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.AccountCircle,
                    contentDescription = "Perfil",
                    tint = if (currentRoute == Routes.ProfileScreen.route) Color(0xFFB27800) else Color.White
                )
            },
            selected = currentRoute == Routes.ProfileScreen.route,
            onClick = { navController.navigate(Routes.ProfileScreen.route) }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = "Amigos",
                    tint = if (currentRoute == Routes.FriendScreen.route) Color(0xFFB27800) else Color.White
                )
            },
            selected = currentRoute == Routes.FriendScreen.route,
            onClick = { navController.navigate(Routes.FriendScreen.route) }
        )
    }
}