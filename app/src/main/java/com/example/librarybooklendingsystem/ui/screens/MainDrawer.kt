package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.R
import com.example.librarybooklendingsystem.ui.models.DrawerItem
import com.example.librarybooklendingsystem.data.AuthState
import com.example.librarybooklendingsystem.data.FirebaseManager
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawer(
    navController: NavController,
    onCloseDrawer: () -> Unit
) {
    val isLoggedIn = AuthState.isLoggedIn
    val currentUser by AuthState.currentUser.collectAsState(initial = null)
    val isAdmin by AuthState.isAdmin.collectAsState(initial = false)
    val userRole by AuthState.currentUserRole.collectAsState(initial = null)
    
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
    ) {
        // Header with organization name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Organization Icon",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF4285F4)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "vanduy1424's\norganization",
                    fontSize = 16.sp,
                    lineHeight = 20.sp
                )
            }
        }

        Divider()

        // Menu Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            DrawerMenuItem(
                icon = Icons.Default.Home,
                text = "Thư viện",
                onClick = {
                    navController.navigate("home") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                    onCloseDrawer()
                }
            )

            DrawerMenuItem(
                icon = Icons.Default.Delete,
                text = "Thùng rác",
                onClick = {
                    if (isLoggedIn) {
                        navController.navigate("trash") {
                            launchSingleTop = true
                        }
                    } else {
                        navController.navigate("login")
                    }
                    onCloseDrawer()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))



        // User info at bottom
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Avatar",
                modifier = Modifier.size(32.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (isLoggedIn) currentUser?.displayName ?: "Người dùng" else "Khách",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isLoggedIn) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            FirebaseManager.signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                            onCloseDrawer()
                        },
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = Color.DarkGray,
                fontSize = 14.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
} 