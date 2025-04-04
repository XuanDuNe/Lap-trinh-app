package com.example.librarybooklendingsystem

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.ui.screens.AccountScreen
import com.example.librarybooklendingsystem.ui.screens.AdminDashboard
import com.example.librarybooklendingsystem.ui.screens.BorrowBookScreen
import com.example.librarybooklendingsystem.ui.screens.BookDetailsScreen
import com.example.librarybooklendingsystem.ui.screens.BottomNavigationBar
import com.example.librarybooklendingsystem.ui.screens.HomeScreen
import com.example.librarybooklendingsystem.ui.screens.LoginScreen
import com.example.librarybooklendingsystem.ui.screens.SignUpScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        val db = FirebaseFirestore.getInstance()

        if (FirebaseApp.getApps(this).isNotEmpty()) {
            Log.d("FirebaseCheck", "Firebase đã kết nối thành công!")
        } else {
            Log.e("FirebaseCheck", "Firebase chưa kết nối!")
        }

        setContent {
            MainScreen() // Show the main screen
        }
    }
}

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "login" && currentRoute != "signup") {
                BottomNavigationBar(navController = navController) // Show bottom navigation bar
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home", // Set initial destination
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("bookDetails") { BookDetailsScreen(navController) } // Book details page
            composable("borrowBook") { BorrowBookScreen(navController) } // Borrow book page
            composable("account") { AccountScreen(navController) } // Account page
            composable("login") { LoginScreen(navController) } // Login page
            composable("signup") { SignUpScreen(navController) } // Sign-up page
        }
    }
}
