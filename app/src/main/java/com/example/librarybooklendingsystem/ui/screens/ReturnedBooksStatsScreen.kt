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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.R
import com.example.librarybooklendingsystem.data.Borrow
import com.example.librarybooklendingsystem.data.FirebaseManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ReturnDetail(
    val userName: String,
    val bookTitle: String,
    val returnDate: Date?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReturnedBooksStatsScreen(navController: NavController) {
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
    var totalReturnedBooks by remember { mutableStateOf(0) }
    var categoryStats by remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var categoryReturnDetails by remember { mutableStateOf<List<ReturnDetail>>(emptyList()) }
    val scope = rememberCoroutineScope()
    
    // Format ngày tháng
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    // Hàm để lấy chi tiết trả sách của một thể loại
    suspend fun getCategoryReturnDetails(category: String) {
        try {
            val details = mutableListOf<ReturnDetail>()
            borrows.forEach { borrow ->
                if (borrow.status == "Đã trả") {
                    val book = FirebaseManager.getBookById(borrow.bookId)
                    if (book?.category == category) {
                        // Lấy tên người dùng từ userId
                        val userName = FirebaseManager.getUserName(borrow.userId)
                        details.add(
                            ReturnDetail(
                                userName = userName,
                                bookTitle = borrow.bookTitle,
                                returnDate = borrow.actualReturnDate
                            )
                        )
                    }
                }
            }
            categoryReturnDetails = details
        } catch (e: Exception) {
            categoryReturnDetails = emptyList()
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
                    if (borrow.status == "Đã trả") {
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
                totalReturnedBooks = total
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
                        text = "Chi tiết sách đã trả - $selectedCategory",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (categoryReturnDetails.isEmpty()) {
                        Text(
                            text = "Không có thông tin trả sách",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn {
                            items(categoryReturnDetails) { detail ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "Người trả: ${detail.userName}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Sách: ${detail.bookTitle}",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                    Text(
                                        text = "Ngày trả: ${detail.returnDate?.let { dateFormat.format(it) } ?: "Không có thông tin"}",
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
                            text = "Thống kê sách đã trả",
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
                        text = "Thống kê sách đã trả theo thể loại",
                        fontSize = 16.sp,
                        color = Color(0xFF0288D1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tổng số lượt trả: $totalReturnedBooks",
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
                    items(categories) { category ->
                        val count = categoryStats[category] ?: 0
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedCategory = category
                                    scope.launch {
                                        getCategoryReturnDetails(category)
                                    }
                                },
                            shape = RoundedCornerShape(8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                                    text = "$count lượt",
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