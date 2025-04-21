package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.librarybooklendingsystem.data.AuthState
import com.example.librarybooklendingsystem.ui.viewmodels.CategoryViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun BottomNavigationBar(
    navController: NavController,
    onOpenDrawer: () -> Unit = {},
    categoryViewModel: CategoryViewModel = viewModel()
) {
    var selectedItem by remember { mutableStateOf(0) }
    var showLoginDialog by remember { mutableStateOf(false) }
    
    val isAdmin by AuthState.isAdmin.collectAsStateWithLifecycle()
    val userRole by AuthState.currentUserRole.collectAsStateWithLifecycle()
    val isLoggedIn by AuthState.isLoggedIn.collectAsStateWithLifecycle()

    val items = listOfNotNull(
        BottomNavItem("Trang chủ", Icons.Default.Home, "home"),
        BottomNavItem("Danh mục", Icons.Default.List, "drawer"),
        if (isAdmin) BottomNavItem("Thống kê", Icons.Default.Settings, "admin_dashboard") else null,
        BottomNavItem("Cá nhân", Icons.Default.Person, "account")
    ).filterNotNull()

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { 
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(32.dp)
                    )
                },
                label = { Text(item.title) },
                selected = selectedItem == index,
                onClick = {
                    when (item.route) {
                        "drawer" -> {
                            onOpenDrawer()
                        }
                        "home" -> {
                            selectedItem = index
                            categoryViewModel.clearSelectedCategory()
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                        "admin_dashboard" -> {
                            if (isAdmin) {
                                selectedItem = index
                                navigateToTab(navController, item.route)
                            }
                        }
                        "account" -> {
                            if (isLoggedIn) {
                                selectedItem = index
                                navigateToTab(navController, item.route)
                            } else {
                                showLoginDialog = true
                            }
                        }
                    }
                }
            )
        }
    }

    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Thông báo") },
            text = { Text("Vui lòng đăng nhập để truy cập") },
            confirmButton = {
                TextButton(onClick = {
                    showLoginDialog = false
                    navController.navigate("login")
                }) {
                    Text("Đăng nhập")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoginDialog = false }) {
                    Text("Hủy")
                }
            }
        )
    }
}

private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)


