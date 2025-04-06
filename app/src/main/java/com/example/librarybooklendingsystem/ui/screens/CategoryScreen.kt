package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.ui.components.CommonHeader

data class CategoryData(
    val name: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {
    // Danh sách các danh mục sách với icon tương ứng
    val categories = listOf(
        CategoryData("Văn học", Icons.Filled.Star),
        CategoryData("Khoa học", Icons.Filled.Star),
        CategoryData("Công nghệ", Icons.Filled.Settings),
        CategoryData("Kinh tế", Icons.Filled.Info),
        CategoryData("Lịch sử", Icons.Filled.Home),
        CategoryData("Địa lý", Icons.Filled.List),
        CategoryData("Nghệ thuật", Icons.Filled.Edit),
        CategoryData("Thể thao", Icons.Filled.Star),
        CategoryData("Sức khỏe", Icons.Filled.Favorite),
        CategoryData("Tâm lý", Icons.Filled.Person)
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
                        navController.navigate("category/${category.name}")
                    }
                )
            }
        }
    }
}

@Composable
fun CategoryItem(category: CategoryData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = Color(0xFF0093AB),
                    modifier = Modifier.size(32.dp)
                )
                Text(
                    text = category.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0093AB)
                )
            }
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Navigate",
                tint = Color(0xFF0093AB),
                modifier = Modifier.size(24.dp)
            )
        }
    }
} 