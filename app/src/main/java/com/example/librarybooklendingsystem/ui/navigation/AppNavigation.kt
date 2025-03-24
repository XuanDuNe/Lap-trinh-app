package com.example.librarybooklendingsystem.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.librarybooklendingsystem.ui.screens.BorrowBookScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "borrowBook") {
        composable("borrowBook") {
            BorrowBookScreen()
        }
    }
}
