package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.ui.components.CommonHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {
    // Danh sách các danh mục sách
    val categories = listOf(
        "Văn học",
        "Khoa học",
        "Công nghệ",
        "Kinh tế",
        "Lịch sử",
        "Địa lý",
        "Nghệ thuật",
        "Thể thao",
        "Sức khỏe",
        "Tâm lý"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CommonHeader(
            title = "Danh mục",
            onBackClick = { navController.navigateUp() },
            onShareClick = { /* Share action */ }
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    onClick = {
                        // Điều hướng đến màn hình hiển thị sách theo danh mục
                        navController.navigate("category/$category")
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = category,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0093AB)
            )
        }
    }
} 