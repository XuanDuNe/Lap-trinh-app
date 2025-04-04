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
import com.example.librarybooklendingsystem.ui.screens.AccountManagementScreen
import com.example.librarybooklendingsystem.ui.screens.AccountScreen
import com.example.librarybooklendingsystem.ui.screens.AdminDashboardScreen
import com.example.librarybooklendingsystem.ui.screens.AuthState
import com.example.librarybooklendingsystem.ui.screens.BorrowBookScreen
import com.example.librarybooklendingsystem.ui.screens.BookDetailsScreen
import com.example.librarybooklendingsystem.ui.screens.BorrowedBooksStatsScreen
import com.example.librarybooklendingsystem.ui.screens.BottomNavigationBar
import com.example.librarybooklendingsystem.ui.screens.CreateAdminScreen
import com.example.librarybooklendingsystem.ui.screens.HomeScreen
import com.example.librarybooklendingsystem.ui.screens.LibraryStatsScreen
import com.example.librarybooklendingsystem.ui.screens.LoginScreen
import com.example.librarybooklendingsystem.ui.screens.PendingBooksApprovalScreen
import com.example.librarybooklendingsystem.ui.screens.ReturnedBooksStatsScreen
import com.example.librarybooklendingsystem.ui.screens.SignUpScreen
import com.example.librarybooklendingsystem.ui.screens.UserStatsScreen
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

        // Initialize AuthState
        AuthState.init(this)

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
    val isAdmin by AuthState.isAdmin.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        // Check login state and redirect if necessary
        if (AuthState.isLoggedIn) {
            AuthState.updateUserRole { success ->
                if (success) {
                    // Chỉ điều hướng nếu đang ở màn hình login hoặc signup
                    if (currentRoute == "login" || currentRoute == "signup") {
                        if (isAdmin) {
                            navController.navigate("admin_dashboard") {
                                popUpTo(0) { inclusive = true }
                            }
                        } else {
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (currentRoute != "login" && currentRoute != "signup") {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("bookDetails") { BookDetailsScreen(navController) }
            composable("borrowBook") { BorrowBookScreen(navController) }
            composable("account") { AccountScreen(navController) }
            composable("admin_dashboard") { 
                if (isAdmin) {
                    AdminDashboardScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            composable("create_admin") { CreateAdminScreen(navController) }

            // Admin Dashboard Routes
            composable("borrowed_books_stats") { 
                if (isAdmin) {
                    BorrowedBooksStatsScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            composable("user_stats") { 
                if (isAdmin) {
                    UserStatsScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            composable("returned_books_stats") { 
                if (isAdmin) {
                    ReturnedBooksStatsScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            composable("library_stats") { 
                if (isAdmin) {
                    LibraryStatsScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            composable("pending_books_approval") { 
                if (isAdmin) {
                    PendingBooksApprovalScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
            composable("account_management") { 
                if (isAdmin) {
                    AccountManagementScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }
        }
    }
}
