package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavigationBar(navController: NavController) {
    var selectedItem by remember { mutableStateOf(0) }
    var showLoginDialog by remember { mutableStateOf(false) }

    if (showLoginDialog) {
        LoginRequiredDialog(
            onDismiss = { showLoginDialog = false },
            navController = navController
        )
    }

    BottomNavigation(
        backgroundColor = Color(0xFFD3D3D3),
        contentColor = Color(0xFF0B8FAC)
    ) {
        // Trang chủ
        BottomNavigationItem(
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
        BottomNavigationItem(
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
        BottomNavigationItem(
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
    }
}


