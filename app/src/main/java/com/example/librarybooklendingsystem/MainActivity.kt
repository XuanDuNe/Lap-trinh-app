package com.example.librarybooklendingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.ui.screens.AccountScreen
import com.example.librarybooklendingsystem.ui.screens.BorrowBookScreen
import com.example.librarybooklendingsystem.ui.screens.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Khởi tạo NavController
            val navController = rememberNavController()

            // Scaffold để hiển thị BottomNavigationBar và quản lý nội dung
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(navController = navController)  // Thêm BottomNavigationBar vào Scaffold
                }
            ) { paddingValues ->
                // Quản lý điều hướng giữa các màn hình
                NavHost(navController = navController, startDestination = "borrowBook") {
                    composable("borrowBook") { BorrowBookScreen(navController) }  // Màn hình mượn sách
                    composable("account") { AccountScreen(navController) }  // Màn hình cá nhân
                }
            }
        }
    }
}
