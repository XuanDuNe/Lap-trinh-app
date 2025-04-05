package com.example.librarybooklendingsystem.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.R
import com.example.librarybooklendingsystem.data.FirebaseManager
import com.google.firebase.auth.FirebaseAuth
import coil.compose.AsyncImage
import com.example.librarybooklendingsystem.ui.components.CommonHeader
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(navController: NavController) {
    val context = LocalContext.current
    var borrowedBooks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var userName by remember { mutableStateOf("") }
    var numericId by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    // Xử lý nút back của hệ thống
    BackHandler {
        // Không làm gì khi nhấn nút back
    }

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        } else {
            scope.launch {
                try {
                    val userInfo = FirebaseManager.getUserInfo(currentUser.uid)
                    userName = userInfo?.get("name") as? String ?: "Người dùng"
                    
                    // Tạo ID số ngẫu nhiên 6 chữ số
                    val random = Random()
                    numericId = String.format("%06d", random.nextInt(1000000))
                    
                    Log.d("AccountScreen", "Thông tin người dùng: $userInfo")
                    Log.d("AccountScreen", "Tên người dùng: $userName")
                    Log.d("AccountScreen", "Numeric ID: $numericId")

                    val books = FirebaseManager.getUserBorrowedBooks(currentUser.uid)
                    if (books != null) {
                        borrowedBooks = books
                    }
                } catch (e: Exception) {
                    Log.e("AccountScreen", "Lỗi khi lấy thông tin người dùng: ${e.message}")
                } finally {
                    isLoading = false
                }
            }
        }
    }

    if (currentUser == null) {
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CommonHeader(
            title = "Cá nhân",
            onBackClick = { /* Không làm gì khi nhấn nút back */ },
            onShareClick = { /* Share action */ }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0093AB)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString() ?: "U",
                    color = Color.White,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = userName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "ID: $numericId",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .width(120.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0093AB)
                )
            ) {
                Text("Đăng xuất", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Sách đang mượn",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (borrowedBooks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Bạn chưa mượn sách nào")
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    userScrollEnabled = true,
                    content = {
                        items(borrowedBooks) { book ->
                            BorrowedBookGridItem(book)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun BorrowedBookGridItem(book: Map<String, Any>) {
    Log.d("BorrowedBookGridItem", "Book data: $book")
    
    val bookTitle = book["bookTitle"] as? String ?: "Không có tiêu đề"
    val authorName = book["author_name"] as? String ?: "Không có tác giả"
    
    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
    
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val expectedReturnDate = try {
        when (val date = book["expectedReturnDate"]) {
            is Long -> dateFormat.format(Date(date))
            is String -> date
            else -> "Chưa có"
        }
    } catch (e: Exception) {
        Log.e("BorrowedBookGridItem", "Lỗi khi xử lý ngày trả: ${e.message}")
        "Chưa có"
    }

    Column(
        modifier = Modifier
            .padding(4.dp)
            .width(110.dp)
            .height(280.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = book["bookCover"] as? String ?: "",
                contentDescription = "Book Image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = bookTitle,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = authorName,
            fontSize = 11.sp,
            color = Color.Gray,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Ngày mượn: $currentDate",
            fontSize = 11.sp,
            color = Color.Gray
        )
        
        Text(
            text = "Ngày trả dự kiến: $expectedReturnDate",
            fontSize = 11.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    MaterialTheme {
        AccountScreen(rememberNavController())
    }
}