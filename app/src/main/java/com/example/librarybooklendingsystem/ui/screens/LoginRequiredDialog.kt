package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginRequiredDialog(
    onDismiss: () -> Unit,
    navController: NavController
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Yêu cầu đăng nhập") },
        text = { Text("Bạn phải đăng nhập để sử dụng tính năng này") },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    navController.navigate("login")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0093AB)
                )
            ) {
                Text("Đăng nhập", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Hủy", color = Color(0xFF0093AB))
            }
        }
    )
} 