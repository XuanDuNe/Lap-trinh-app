package com.example.librarybooklendingsystem.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.librarybooklendingsystem.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController, startDestination = "home") {
        // Màn hình Trang chủ
        composable("home") {
            HomeScreen(navController = navController)
        }

        // Màn hình Danh mục
        composable("category") {
            CategoryScreen(navController = navController)
        }

        // Màn hình Cá nhân
        composable("account") {
            AccountScreen(navController = navController)
        }

        // Màn hình Mượn sách
        composable("borrowBook/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BorrowBookScreen(navController = navController, bookId = bookId)
        }

        // Màn hình Chi tiết sách
        composable("bookDetails/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            BookDetailsScreen(navController = navController, bookId = bookId)
        }

        // Màn hình Đăng nhập
        composable("login") {
            LoginScreen(navController = navController)
        }

        // Màn hình Đăng ký
        composable("signup") {
            SignUpScreen(navController = navController)
        }

        // Màn hình Admin Dashboard
        composable("admin_dashboard") {
            val isAdmin by AuthState.isAdmin.collectAsStateWithLifecycle(initialValue = false)
            val userRole by AuthState.currentUserRole.collectAsStateWithLifecycle(initialValue = null)
            
            LaunchedEffect(isAdmin, userRole) {
                Log.d("AppNavigation", "Admin status: $isAdmin, User role: $userRole")
                if (!isAdmin || userRole != "admin") {
                    Log.e("AppNavigation", "Non-admin user attempted to access admin dashboard")
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            }
            
            if (isAdmin && userRole == "admin") {
                AdminDashboardScreen(navController = navController)
            }
        }

        // Các màn hình thống kê
        composable("library_stats") {
            LibraryStatsScreen(navController = navController)
        }

        composable("user_stats") {
            UserStatsScreen(navController = navController)
        }

        composable("borrowed_books_stats") {
            BorrowedBooksStatsScreen(navController = navController)
        }

        composable("returned_books_stats") {
            ReturnedBooksStatsScreen(navController = navController)
        }

        composable("pending_books_approval") {
            PendingBooksApprovalScreen(navController = navController)
        }
    }
}
