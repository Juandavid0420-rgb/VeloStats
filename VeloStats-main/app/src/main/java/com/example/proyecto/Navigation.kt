package com.example.proyecto

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import kotlinx.serialization.Serializable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.proyecto.ui.ViewModels.SettingsViewModel
import com.example.proyecto.ui.screens.CreateProfileScreen
import com.example.proyecto.ui.screens.LoginScreen
import com.example.proyecto.ui.screens.FriendScreen
import com.example.proyecto.ui.screens.MainScreen
import com.example.proyecto.ui.screens.ProfileScreen
import com.example.proyecto.ui.screens.RoadScreen
import com.example.proyecto.ui.screens.StartScreen
import com.example.proyecto.ui.screens.WorkoutsScreen
import com.example.proyecto.ui.screens.EditProfileScreen
import com.example.proyecto.ui.screens.SettingsScreen
import com.example.proyecto.ui.screens.TripsScreen
import com.example.proyecto.ui.screens.FriendRoutesScreen




@Serializable
sealed class Routes(val route: String) {
    @Serializable
    object CreateProfileScreen : Routes("create_profile_screen")

    @Serializable
    object FriendScreen : Routes("friend_screen")

    @Serializable
    object LoginScreen : Routes("login_screen")

    @Serializable
    object MainScreen : Routes("main_screen")

    @Serializable
    object ProfileScreen : Routes("profile_screen")

    @Serializable
    object RoadScreen : Routes("road_screen")

    @Serializable
    object StartScreen : Routes("start_screen")

    @Serializable
    object TripsScreen : Routes("trips_screen")


    @Serializable
    object WorkoutsScreen : Routes("workouts_screen")
    @Serializable
    object SettingScreen : Routes("setting_screen")
    @Serializable
    object EditProfileScreen : Routes("edit_profile_screen")

    @Serializable
    object RideTrackingScreen : Routes("ride_tracking_screen") {
        const val ARG_ROUTE_ID = "routeId"
        fun withRoute(routeId: String) = "$route/$routeId"
    }
    @Serializable
    object SharedRoutesScreen : Routes("shared_routes_screen")
}


@Composable
fun NavigationStack(modifier: Modifier = Modifier, viewModel: SettingsViewModel) {
    val navController = rememberNavController() // Crear el NavController
    NavHost(navController = navController, startDestination = Routes.StartScreen.route, modifier = modifier) {
        composable(Routes.CreateProfileScreen.route) {
            CreateProfileScreen(navController = navController)
        }
        composable(Routes.FriendScreen.route) {
            FriendScreen(navController = navController)
        }

        composable(Routes.ProfileScreen.route) {
            ProfileScreen(navController = navController)
        }
        composable(Routes.EditProfileScreen.route) {
            EditProfileScreen(navController = navController)
        }
        composable(Routes.MainScreen.route) {
            MainScreen(navController = navController)
        }

        composable(Routes.RoadScreen.route) {
            RoadScreen(navController = navController)
        }

        composable(Routes.WorkoutsScreen.route) {
            WorkoutsScreen(navController = navController)
        }

        composable(Routes.StartScreen.route) {
            StartScreen(navController = navController) {
                navController.navigate(Routes.LoginScreen.route)
            }
        }
        composable(Routes.LoginScreen.route) {
            LoginScreen(navController = navController)
        }


        composable(Routes.TripsScreen.route) {
            TripsScreen(userId = "user123") // reemplazar "user123" con el ID real
        }

        composable("friend_routes_screen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            FriendRoutesScreen(userId = userId, navController = navController)
        }


    }
}





