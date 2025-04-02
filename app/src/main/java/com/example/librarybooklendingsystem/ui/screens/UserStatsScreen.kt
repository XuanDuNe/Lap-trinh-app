package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.librarybooklendingsystem.R

@Composable
fun UserStatisticsScreen(navController: NavController?) {
    val users = listOf(
        "User 1" to "Lorem ipsum dolor, consectetur.",
        "User 2" to "Lorem ipsum dolor, consectetur.",
        "User 3" to "Lorem ipsum dolor, consectetur.",
        "User 4" to "Lorem ipsum dolor, consectetur.",
        "User 5" to "Lorem ipsum dolor, consectetur.",
        "User 6" to "Lorem ipsum dolor, consectetur.",
        "User 7" to "Lorem ipsum dolor, consectetur.",
        "User 8" to "Lorem ipsum dolor, consectetur."
    )

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Text(
                        text = "Thống kê người dùng",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                backgroundColor = Color(0xFF0288D1),
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.navigate("admin_dashboard") }) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        backgroundColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(users) { user ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "User Icon",
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color.LightGray, shape = CircleShape),
                                tint = Color(0xFF0288D1)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = user.first, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(text = user.second, fontSize = 14.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            IconButton(onClick = { /* Chỉnh sửa */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.edit),
                                    contentDescription = "Edit Icon",
                                    tint = Color(0xFF0288D1)
                                )
                            }
                            IconButton(onClick = { /* Xóa */ }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.delete),
                                    contentDescription = "Delete Icon",
                                    tint = Color.Red
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserStatisticsScreen() {
    UserStatisticsScreen(navController = null)
}
