package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendingBooksApprovalScreen(navController: NavController) {
    val pendingBooks = listOf(
        "Nguyễn Văn A" to "Lập trình Kotlin",
        "Trần Thị B" to "Phát triển Android",
        "Lê Văn C" to "Học Jetpack Compose",
        "Nguyễn Văn A" to "Lập trình Kotlin",
        "Trần Thị B" to "Phát triển Android",
        "Lê Văn C" to "Học Jetpack Compose",
        "Nguyễn Văn A" to "Lập trình Kotlin",
        "Trần Thị B" to "Phát triển Android",
        "Lê Văn C" to "Học Jetpack Compose",
        "Nguyễn Văn A" to "Lập trình Kotlin",
        "Trần Thị B" to "Phát triển Android",
        "Lê Văn C" to "Học Jetpack Compose"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Thống kê sách cần duyệt",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0288D1),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Quay lại",
                            tint = Color.White,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.thongke),
                        contentDescription = "Stats Icon",
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thống kê sách cần duyệt cho mượn",
                        fontSize = 16.sp,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tổng số lượt mượn: ${pendingBooks.size}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(pendingBooks) { book ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "Tên người mượn: ${book.first}",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tên sách: ${book.second}",
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                                Button(
                                    onClick = { /* Xử lý duyệt sách */ },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0288D1)
                                    )
                                ) {
                                    Text(
                                        text = "Duyệt",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

