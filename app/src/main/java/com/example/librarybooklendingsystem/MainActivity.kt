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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("FirebaseCheck", "Firebase được khởi tạo trong MainActivity")
            }
            
            val db = FirebaseFirestore.getInstance()
            Log.d("FirebaseCheck", "Firestore instance created successfully")
            
            // Test connection
            db.collection("books").get()
                .addOnSuccessListener { 
                    Log.d("FirebaseCheck", "Kết nối Firebase thành công! Số lượng sách: ${it.size()}")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseCheck", "Lỗi kết nối Firebase: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "Lỗi khởi tạo Firebase: ${e.message}")
            e.printStackTrace()
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
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("FirebaseCheck", "Firebase được khởi tạo trong Application")
            }
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "Lỗi khởi tạo Firebase trong Application: ${e.message}")
            e.printStackTrace()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignUpScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable(
                route = "bookDetails/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                BookDetailsScreen(navController = navController, bookId = bookId)
            }
            composable(
                route = "borrowbook/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")
                BorrowBookScreen(navController = navController, bookId = bookId)
            }
            composable("borrowbook") { 
                BorrowBookScreen(navController = navController) 
            }
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
            composable("pending_approvals") { PendingBooksApprovalScreen(navController) }
            composable("library_stats") { LibraryStatsScreen(navController) }
            composable("user_stats") { UserStatsScreen(navController) }
            composable("borrowed_books_stats") { BorrowedBooksStatsScreen(navController) }
            composable("returned_books_stats") { ReturnedBooksStatsScreen(navController) }
            composable("account_management") { AccountManagementScreen(navController) }
        }
    }
}
