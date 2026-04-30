package com.example.witnessitproject.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(),
               startDestination:String= ROUTE_SPLASH_SCREEN) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASH_SCREEN) { SplashScreen(navController) }
        composable(ROUTE_REGISTER) { RegisterScreen(navController) }
        composable(ROUTE_LOGIN){ LoginScreen(navController) }
        composable(ROUTE_NEW_REPORT){ NewRecordScreen(navController) }
        composable(ROUTE_DASHBOARD){ HomeScreen(navController) }
        composable("report_detail/{reportId}") { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            RecordDetailScreen(navController = navController, reportId = reportId)

        }
        composable(ROUTE_SEARCH){ SearchScreen(navController)}
        composable(ROUTE_PROFILE){ ProfileScreen(navController)}
        composable(ROUTE_MY_REPORTS){ MyReportsScreen(navController)}
        composable("${ROUTE_EDIT_REPORT}/{reportId}") { backStackEntry ->
            val reportId = backStackEntry.arguments?.getString("reportId") ?: ""
            EditReportScreen(navController = navController, reportId = reportId)
        }



    }

}