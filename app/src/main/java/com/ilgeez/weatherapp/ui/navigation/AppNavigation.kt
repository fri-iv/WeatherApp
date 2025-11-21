package com.ilgeez.weatherapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ilgeez.weatherapp.ui.screens.WeatherScreen

sealed class Screen(val route: String) {
    object Weather : Screen("weather")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Weather.route) {
        composable(Screen.Weather.route) {
            WeatherScreen()
        }
    }
}
