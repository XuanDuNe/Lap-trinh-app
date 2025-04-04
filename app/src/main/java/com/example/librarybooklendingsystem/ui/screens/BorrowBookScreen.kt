package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.R
import com.example.librarybooklendingsystem.data.Book
import com.example.librarybooklendingsystem.data.FirebaseManager
import com.example.librarybooklendingsystem.ui.screens.AuthState
import coil.compose.AsyncImage
import com.example.librarybooklendingsystem.ui.components.CommonHeader
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BorrowBookScreen(
    navController: NavController,
    bookId: String? = null
) {
    val studentName = remember { mutableStateOf(TextFieldValue("")) }
    val expectedReturnDate = remember { mutableStateOf(TextFieldValue("")) }
    var book by remember { mutableStateOf<Book?>(null) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Load book data if bookId is provided
    LaunchedEffect(bookId) {
        if (bookId != null) {
            scope.launch {
                book = FirebaseManager.getBookById(bookId)
            }
        }
    }

    if (showError) {
        AlertDialog(
            onDismissRequest = { showError = false },
            title = { Text("Lỗi") },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showError = false }) {
                    Text("Đóng")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CommonHeader(
            title = "Mượn sách",
            onBackClick = { navController.navigateUp() },
            onShareClick = { /* Share action */ }
        )

        // Nội dung trang mượn sách
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (book != null) {
                // Hiển thị ảnh sách từ URL
                AsyncImage(
                    model = book!!.coverUrl,
                    contentDescription = "Book Image",
                    modifier = Modifier
                        .size(220.dp)
                        .padding(bottom = 16.dp)
                        .offset(y = 50.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(50.dp))

                // Tên sách
                Text(
                    text = book!!.title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } else {
                // Hiển thị loading hoặc placeholder
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .padding(bottom = 16.dp)
                        .offset(y = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // Ô nhập họ và tên
            OutlinedTextField(
                value = studentName.value,
                onValueChange = { studentName.value = it },
                label = { Text("Nhập họ và tên") },
                leadingIcon = {
                    Icon(Icons.Filled.Person, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // Ô nhập ngày dự kiến trả
            OutlinedTextField(
                value = expectedReturnDate.value,
                onValueChange = { expectedReturnDate.value = it },
                label = { Text("Ngày dự kiến trả") },
                leadingIcon = {
                    Icon(Icons.Filled.DateRange, contentDescription = null)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Nút xác nhận mượn sách
            Button(
                onClick = {
                    if (studentName.value.text.isEmpty() || expectedReturnDate.value.text.isEmpty()) {
                        errorMessage = "Vui lòng điền đầy đủ thông tin"
                        showError = true
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        try {
                            val borrowData = mapOf(
                                "userId" to (AuthState.currentUserId ?: ""),
                                "bookId" to (bookId ?: ""),
                                "studentName" to studentName.value.text,
                                "expectedReturnDate" to expectedReturnDate.value.text,
                                "status" to FirebaseManager.BorrowStatus.PENDING,
                                "borrowDate" to Date(),
                                "bookTitle" to (book?.title ?: ""),
                                "bookCover" to (book?.coverUrl ?: "")
                            )

                            FirebaseManager.createBorrowRequest(borrowData)
                            navController.navigate("account") {
                                popUpTo("borrowbook/${bookId}") { inclusive = true }
                            }
                        } catch (e: Exception) {
                            errorMessage = e.message ?: "Đã xảy ra lỗi khi gửi yêu cầu mượn sách"
                            showError = true
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .width(250.dp)
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0093AB)
                ),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(text = "Xác nhận", fontSize = 20.sp)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BorrowBookScreenPreview() {
    BorrowBookScreen(navController = rememberNavController())
}