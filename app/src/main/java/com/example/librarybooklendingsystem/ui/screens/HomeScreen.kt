package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(navController: NavController) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var currentBannerIndex by remember { mutableStateOf(0) }
    
    // Danh sách các banner
    val banners = listOf(
        R.drawable.logouth,
        R.drawable.yeu, // Thay bằng hình ảnh thực tế của bạn
        R.drawable.yeumbangmatgiuembangtim  // Thay bằng hình ảnh thực tế của bạn
    )

    // Tự động chuyển đổi banner mỗi 3 giây
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentBannerIndex = (currentBannerIndex + 1) % banners.size
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(100.dp),
                backgroundColor =(Color(0xFF0B8FAC))
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(color = Color.White, shape = RoundedCornerShape(25.dp)),
                    placeholder = { Text("Nhập từ khóa tìm kiếm") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon"
                        )
                    },
                    shape = RoundedCornerShape(25.dp)
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(scrollState)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(16.dp),
                elevation = 4.dp
            ) {
                Image(
                    painter = painterResource(id = banners[currentBannerIndex]),
                    contentDescription = "Banner",
                    contentScale = ContentScale.Crop
                )
            }

            // Top 10 đọc nhiều
            SectionHeader(title = "Top 10 đọc nhiều")
            BookList(navController = navController)

            // Sách mới
            SectionHeader(title = "Sách mới")
            BookList(navController = navController)
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(16.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun BookList(navController: NavController) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(3) { index ->
            BookItem(
                navController = navController,
                bookTitle = "Tên sách $index",
                bookImage = R.drawable.logouth
            )
        }
    }
}

@Composable
fun BookItem(
    navController: NavController,
    bookTitle: String,
    bookImage: Int
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable { 
                navController.navigate("bookDetails")
            },
        elevation = 4.dp
    ) {
        Column {
            Image(
                painter = painterResource(id = bookImage),
                contentDescription = "Book Cover",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = bookTitle,
                modifier = Modifier.padding(8.dp),
                maxLines = 2
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun BookItemPreview() {
    BookItem(navController = rememberNavController(), bookTitle = "Tên sách 1", bookImage = R.drawable.logouth)
}

@Preview(showBackground = true)
@Composable
fun SectionHeaderPreview() {
    SectionHeader(title = "Top 10 đọc nhiều")
}

