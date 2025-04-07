package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.R
import com.example.librarybooklendingsystem.data.Borrow
import com.example.librarybooklendingsystem.data.FirebaseManager
import kotlinx.coroutines.launch

data class BorrowDetail(
    val userName: String,
    val bookTitle: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowedBooksStatsScreen(navController: NavController) {
    // Định nghĩa các thể loại sách
    val categories = listOf(
        "Tiểu thuyết",
        "Văn học",
        "Văn học thiếu nhi",
        "Khoa học viễn tưởng",
        "Kỹ năng sống",
        "Văn Học"
    )

    var borrows by remember { mutableStateOf<List<Borrow>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var totalBorrowedBooks by remember { mutableStateOf(0) }
    var categoryStats by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var categoryBorrowDetails by remember { mutableStateOf<List<BorrowDetail>>(emptyList()) }
    val scope = rememberCoroutineScope()

    // Hàm để lấy chi tiết mượn sách của một thể loại
    suspend fun getCategoryBorrowDetails(category: String) {
        try {
            val details = mutableListOf<BorrowDetail>()
            borrows.forEach { borrow ->
                if (borrow.status == "Đang mượn") {
                    val book = FirebaseManager.getBookById(borrow.bookId)
                    if (book?.category == category) {
                        // Lấy tên người dùng từ userId
                        val userName = FirebaseManager.getUserName(borrow.userId)
                        details.add(
                            BorrowDetail(
                                userName = userName,
                                bookTitle = borrow.bookTitle
                            )
                        )
                    }
                }
            }
            categoryBorrowDetails = details
        } catch (e: Exception) {
            categoryBorrowDetails = emptyList()
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                borrows = FirebaseManager.getAllBorrows()
                
                // Tạo map với các thể loại từ danh sách đã định nghĩa
                val stats = categories.associateWith { 0 }.toMutableMap()
                var total = 0
                
                // Duyệt qua từng lượt mượn
                borrows.forEach { borrow ->
                    if (borrow.status == "Đang mượn") {
                        // Lấy thông tin sách từ book_id
                        val book = FirebaseManager.getBookById(borrow.bookId)
                        if (book != null) {
                            val category = book.category
                            if (category.isNotEmpty()) {
                                stats[category] = (stats[category] ?: 0) + 1
                                total++
                            }
                        }
                    }
                }
                
                categoryStats = stats
                totalBorrowedBooks = total
                isLoading = false
            } catch (e: Exception) {
                isLoading = false
            }
        }
    }

    if (selectedCategory != null) {
        Dialog(onDismissRequest = { selectedCategory = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Chi tiết mượn sách - $selectedCategory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (categoryBorrowDetails.isEmpty()) {
                        Text(
                            text = "Không có thông tin mượn sách",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn {
                            items(categoryBorrowDetails) { detail ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Người mượn: ${detail.userName}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Sách: ${detail.bookTitle}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                Divider()
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = { selectedCategory = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Đóng")
                    }
                }
            }
        }
    }

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
                            text = "Thống kê mượn sách",
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
                        text = "Thống kê tình hình mượn sách theo thể loại",
                        fontSize = 16.sp,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tổng số sách đang mượn: $totalBorrowedBooks",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF0288D1)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Hiển thị danh sách các thể loại đã định nghĩa trước
                    items(categories) { category ->
                        val count = categoryStats[category] ?: 0
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedCategory = category
                                    scope.launch {
                                        getCategoryBorrowDetails(category)
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$count cuốn",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    
                    // Hiển thị các thể loại khác nếu có
                    val otherCategories = categoryStats.keys.filter { it !in categories }
                    items(otherCategories) { category ->
                        val count = categoryStats[category] ?: 0
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$count cuốn",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}