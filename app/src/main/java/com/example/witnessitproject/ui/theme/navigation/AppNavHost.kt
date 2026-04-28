package com.example.witnessitproject.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.witnessitproject.ui.theme.screens.dashboard.HomeScreen
import com.example.witnessitproject.ui.theme.screens.login.LoginScreen
import com.example.witnessitproject.ui.theme.screens.record.NewRecordScreen
import com.example.witnessitproject.ui.theme.screens.register.RegisterScreen
import com.example.witnessitproject.ui.theme.screens.splashscreen.SplashScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_SPLASH_SCREEN
    ) {
        composable(ROUTE_SPLASH_SCREEN) { SplashScreen(navController) }
        composable(ROUTE_REGISTER) { RegisterScreen(navController) }
        composable(ROUTE_LOGIN){ LoginScreen(navController) }
        composable(ROUTE_NEW_REPORT){ NewRecordScreen(navController) }
        composable(ROUTE_DASHBOARD){ HomeScreen(navController) }

    }

}