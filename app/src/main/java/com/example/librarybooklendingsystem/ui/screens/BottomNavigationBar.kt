package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val userRole by AuthState.currentUserRole.collectAsState(initial = null)

    if (showLoginDialog) {
        LoginRequiredDialog(
            onDismiss = { showLoginDialog = false },
            navController = navController
        )
    }

    NavigationBar(
        containerColor = Color(0xFFD3D3D3),
        contentColor = Color(0xFF0B8FAC)
    ) {
        // Trang chủ
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Trang chủ",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = { Text("Trang chủ") },
            selected = selectedItem == 0,
            onClick = {
                selectedItem = 0
                navController.navigate("home")
            }
        )

        // Danh mục
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.List,
                    contentDescription = "Danh mục",
                    modifier = Modifier.size(38.dp)
                )
            },
            label = { Text("Danh mục") },
            selected = selectedItem == 1,
            onClick = {
                selectedItem = 1
                navController.navigate("category")
            }
        )

        // Cá nhân
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Filled.Person,
                    contentDescription = "Cá nhân",
                    modifier = Modifier.size(32.dp)
                )
            },
            label = { Text("Cá nhân") },
            selected = selectedItem == 2,
            onClick = {
                selectedItem = 2
                if (AuthState.isLoggedIn) {
                    navController.navigate("account")
                } else {
                    showLoginDialog = true
                }
            }
        )

        // Thống kê (chỉ hiển thị cho admin)
        if (userRole == "admin") {
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Filled.Settings,
                        contentDescription = "Thống kê",
                        modifier = Modifier.size(32.dp)
                    )
                },
                label = { Text("Thống kê") },
                selected = selectedItem == 3,
                onClick = {
                    selectedItem = 3
                    navController.navigate("admin_dashboard")
                }
            )
        }
    }
}


