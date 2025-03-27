package com.example.librarybooklendingsystem.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.librarybooklendingsystem.ui.screens.BorrowBookScreen
import com.example.librarybooklendingsystem.ui.screens.AccountScreen

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "borrowBook") {
        // Màn hình Mượn Sách
        composable("borrowBook") {
            BorrowBookScreen(navController = navController)
        }

        // Màn hình Cá Nhân
        composable("account") {
            AccountScreen(navController = navController)
        }

        // Bạn có thể thêm các composable khác tương tự ở đây nếu có
    }
}
