package com.example.librarybooklendingsystem.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.ui.screens.*

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("login") {
            LoginScreen(navController)
        }
        composable("signup") {
            SignUpScreen(navController)
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("account") {
            AccountScreen(navController)
        }
        composable("book_details/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            if (bookId != null) {
                BookDetailsScreen(navController, bookId)
            }
        }
        composable("admin_dashboard") {
            AdminDashboardScreen(navController = navController)
        }
    }
} 