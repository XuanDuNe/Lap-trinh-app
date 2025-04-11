package com.example.librarybooklendingsystem.ui.screens

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
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
    var showReturnDialog by remember { mutableStateOf(false) }
    var selectedBook by remember { mutableStateOf<Map<String, Any>?>(null) }

    // Dialog xác nhận trả sách
    if (showReturnDialog) {
        AlertDialog(
            onDismissRequest = {
                showReturnDialog = false
                selectedBook = null
            },
            title = { Text("Xác nhận trả sách") },
            text = { Text("Bạn có chắc chắn muốn trả sách này không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            selectedBook?.let { book ->
                                try {
                                    val bookId = book["bookId"] as? String
                                    if (bookId != null) {
                                        // Cập nhật trạng thái sách trong Firestore
                                        FirebaseManager.returnBook(currentUser?.uid ?: "", book)

                                        // Xóa sách khỏi danh sách hiển thị ngay lập tức
                                        borrowedBooks = borrowedBooks.filter {
                                            (it["bookId"] as? String) != bookId
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("AccountScreen", "Lỗi khi trả sách: ${e.message}")
                                }
                            }
                            showReturnDialog = false
                            selectedBook = null
                        }
                    }
                ) {
                    Text("Có")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showReturnDialog = false
                        selectedBook = null
                    }
                ) {
                    Text("Không")
                }
            }
        )
    }

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
            showShareButton = false,
            showBackButton =  false
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(borrowedBooks) { book ->
                        BorrowedBookGridItem(
                            book = book,
                            onBookClick = {
                                selectedBook = book
                                showReturnDialog = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BorrowedBookGridItem(
    book: Map<String, Any>,
    onBookClick: () -> Unit
) {
    Log.d("BorrowedBookGridItem", "Book data: $book")

    val bookTitle = book["bookTitle"] as? String ?: "Không có tiêu đề"
    val authorName = book["author_name"] as? String ?: "Không có tác giả"
    val status = book["status"] as? String ?: "Không xác định"

    val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val expectedReturnDate = try {
        when (val date = book["expectedReturnDate"]) {
            is com.google.firebase.Timestamp -> dateFormat.format(date.toDate())
            is String -> date
            else -> "Chưa có"
        }
    } catch (e: Exception) {
        Log.e("BorrowedBookGridItem", "Lỗi khi xử lý ngày trả: ${e.message}")
        "Chưa có"
    }

    Card(
        modifier = Modifier
            .padding(4.dp)
            .width(110.dp)
            .height(200.dp)
            .clickable { onBookClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            AsyncImage(
                model = book["bookCover"] as? String ?: "",
                contentDescription = "Book Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = bookTitle,
                    maxLines = 1,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 12.sp
                )
                Text(
                    text = authorName,
                    maxLines = 1,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 10.sp,
                    modifier = Modifier.offset(y = (-2).dp)
                )
                Text(
                    text = "Mượn: $currentDate",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 9.sp,
                    modifier = Modifier.offset(y = (-2).dp)
                )
                Text(
                    text = "Trả: $expectedReturnDate",
                    fontSize = 9.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 9.sp,
                    modifier = Modifier.offset(y = (-2).dp)
                )
                Text(
                    text = if (status == "Chờ duyệt trả") "Đang chờ duyệt trả" else "Đang mượn",
                    fontSize = 9.sp,
                    color = if (status == "Chờ duyệt trả") Color(0xFFFFA000) else Color(0xFF4CAF50),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 9.sp,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    MaterialTheme {
        AccountScreen(rememberNavController())
    }
}