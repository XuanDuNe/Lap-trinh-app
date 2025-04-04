package com.example.librarybooklendingsystem

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
        if (FirebaseApp.getApps(this).isNotEmpty()) {
            Log.d("FirebaseCheck", "Firebase đã kết nối thành công!")
        } else {
            Log.e("FirebaseCheck", "Firebase chưa kết nối!")
        }
        setContent {
            MainScreen()
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
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("bookDetails") { BookDetailsScreen(navController)}
            composable("borrowBook") { BorrowBookScreen(navController) }
            composable("account") { AccountScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
        }
    }
}
