package com.example.witnessitproject.ui.theme.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.Modifier
import com.example.witnessitproject.ui.theme.screens.admin.AdminScreen
import com.example.witnessitproject.ui.theme.screens.admin.AuthorityScreen
import com.example.witnessitproject.ui.theme.screens.dashboard.BottomNavBar
import com.example.witnessitproject.ui.theme.screens.dashboard.HomeScreen
import com.example.witnessitproject.ui.theme.screens.login.LoginScreen
import com.example.witnessitproject.ui.theme.screens.myreport.MyReportsScreen
import com.example.witnessitproject.ui.theme.screens.profile.ProfileScreen
import com.example.witnessitproject.ui.theme.screens.record.EditReportScreen
import com.example.witnessitproject.ui.theme.screens.record.NewRecordScreen
import com.example.witnessitproject.ui.theme.screens.record.RecordDetailScreen
import com.example.witnessitproject.ui.theme.screens.register.RegisterScreen
import com.example.witnessitproject.ui.theme.screens.search.SearchScreen
import com.example.witnessitproject.ui.theme.screens.splashscreen.SplashScreen


val noBottomBarRoutes = listOf(
    ROUTE_SPLASH_SCREEN,
    ROUTE_LOGIN,
    ROUTE_REGISTER,
    ROUTE_ADMIN,
    ROUTE_AUTHORITY
)

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH_SCREEN
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route


    val showBottomBar = currentRoute != null &&
            noBottomBarRoutes.none { currentRoute.startsWith(it) }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(ROUTE_SPLASH_SCREEN) {
                SplashScreen(navController)
            }
            composable(ROUTE_REGISTER) {
                RegisterScreen(navController)
            }
            composable(ROUTE_LOGIN) {
                LoginScreen(navController)
            }
            composable(ROUTE_DASHBOARD) {
                HomeScreen(navController)
            }
            composable(ROUTE_NEW_REPORT) {
                NewRecordScreen(navController)
            }
            composable("${ROUTE_RECORD_DETAIL}/{reportId}") { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
                RecordDetailScreen(navController = navController, reportId = reportId)
            }
            composable("${ROUTE_EDIT_REPORT}/{reportId}") { backStackEntry ->
                val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
                EditReportScreen(navController = navController, reportId = reportId)
            }
            composable(ROUTE_SEARCH) {
                SearchScreen(navController)
            }
            composable(ROUTE_MY_REPORTS) {
                MyReportsScreen(navController)
            }
            composable(ROUTE_PROFILE) {
                ProfileScreen(navController)
            }
            composable(ROUTE_ADMIN) {
                AdminScreen(navController)
            }
            composable(ROUTE_AUTHORITY) {
                AuthorityScreen(navController)
            }
        }
    }
}