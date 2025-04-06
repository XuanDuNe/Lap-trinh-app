package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.ui.components.CommonHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(navController: NavController) {
    Column {
        CommonHeader(
            title = "Thư viện",
            onBackClick = { navController.navigateUp() }
        )
        // Content will be added later
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLibraryScreen(navController: NavController) {
    Column {
        CommonHeader(
            title = "My Library",
            onBackClick = { navController.navigateUp() }
        )
        // Content will be added later
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(navController: NavController) {
    Column {
        CommonHeader(
            title = "Thành viên",
            onBackClick = { navController.navigateUp() }
        )
        // Content will be added later
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesScreen(navController: NavController) {
    Column {
        CommonHeader(
            title = "Vật tư",
            onBackClick = { navController.navigateUp() }
        )
        // Content will be added later
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeScreen(navController: NavController) {
    Column {
        CommonHeader(
            title = "Nâng cấp",
            onBackClick = { navController.navigateUp() }
        )
        // Content will be added later
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    Column {
        CommonHeader(
            title = "Cài đặt",
            onBackClick = { navController.navigateUp() }
        )
        // Content will be added later
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrashScreen(navController: NavController) {
    Column {
        CommonHeader(
            title = "Thùng rác",
            onBackClick = { navController.navigateUp() }
        )
        // Content will be added later
    }
} 