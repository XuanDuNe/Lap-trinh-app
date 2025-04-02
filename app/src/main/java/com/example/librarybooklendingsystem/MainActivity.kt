package com.example.librarybooklendingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.ui.screens.AccountScreen
import com.example.librarybooklendingsystem.ui.screens.AdminDashboard
import com.example.librarybooklendingsystem.ui.screens.BorrowBookScreen
import com.example.librarybooklendingsystem.ui.screens.BookDetailsScreen
import com.example.librarybooklendingsystem.ui.screens.BottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "bookDetails",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("bookDetails") { BookDetailsScreen(navController)}
            composable("borrowBook") { BorrowBookScreen(navController) }
            composable("account") { AccountScreen(navController) }
        }
    }
}
