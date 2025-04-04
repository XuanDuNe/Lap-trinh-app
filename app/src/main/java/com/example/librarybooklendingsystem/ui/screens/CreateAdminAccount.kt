package com.example.librarybooklendingsystem.ui.screens

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class CreateAdminAccount : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                CreateAdminAccountScreen()
            }
        }
    }
}

@Composable
fun CreateAdminAccountScreen() {
    val context = LocalContext.current
    var adminUid by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // Thông tin tài khoản admin
    val adminEmail = "2251120072@ut.edu.vn"
    val adminPassword = "vanduy1424."

    LaunchedEffect(Unit) {
        AuthState.createAdminAccount(
            email = adminEmail,
            password = adminPassword,
            context = context,
            onSuccess = {
                adminUid = AuthState.getCurrentUser()?.uid ?: ""
                isLoading = false
            },
            onError = { error ->
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                isLoading = false
            }
        )
    }

    // Hiển thị thông báo đang tạo tài khoản và UID
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Creating admin account and documents...",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        if (isLoading) {
            CircularProgressIndicator()
        }
        if (adminUid.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Admin UID: $adminUid",
                fontSize = 16.sp
            )
        }
    }
} 