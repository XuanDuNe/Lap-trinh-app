package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.ui.tooling.preview.Preview
import com.example.librarybooklendingsystem.R

@Composable
fun ReturnBooksStats(navController: NavController?) {
    val bookCategories = listOf(
        "Thể loại 1" to 5,
        "Thể loại 2" to 5,
        "Thể loại 3" to 5,
        "Thể loại 4" to 5,
        "Thể loại 1" to 5,
        "Thể loại 2" to 5,
        "Thể loại 3" to 5,
        "Thể loại 4" to 5,
        "Thể loại 1" to 5,
        "Thể loại 2" to 5,
        "Thể loại 3" to 5,
        "Thể loại 4" to 5,"Thể loại 1" to 5,
        "Thể loại 2" to 5,
        "Thể loại 3" to 5,
        "Thể loại 4" to 5,
        "Thể loại 1" to 5,
        "Thể loại 2" to 5,
        "Thể loại 3" to 5,
        "Thể loại 4" to 5
    )

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp),
                title = {
                    Text(
                        text = "Thống kê trả sách",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                backgroundColor = Color(0xFF0288D1),
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(
                            Icons.Filled.KeyboardArrowLeft, contentDescription = "Back",
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.thongke),
                        contentDescription = "Stats Icon",
                        modifier = Modifier.size(40.dp),
                        tint = Color(0xFF0288D1)
                    )
                    Text(
                        text = "Thống kê tình hình mượn sách theo thể loại",
                        fontSize = 16.sp,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tổng số lượt mượn: 20",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(bookCategories) { category ->
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = category.first, fontSize = 16.sp)
                            Text(text = "${category.second} lượt mượn", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewReturnBooksStats() {
    ReturnBooksStats(navController = null)
}