package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.librarybooklendingsystem.data.Book
import com.example.librarybooklendingsystem.data.FirebaseManager
import com.example.librarybooklendingsystem.ui.components.CommonHeader
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontStyle
import com.example.librarybooklendingsystem.data.AuthState
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(navController: NavController, bookId: String) {
    var book by remember { mutableStateOf<Book?>(null) }
    val scope = rememberCoroutineScope()
    val isLoggedIn by AuthState.isLoggedIn.collectAsStateWithLifecycle()
    var showLoginDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        scope.launch {
            book = FirebaseManager.getBookById(bookId)
        }
    }

    if (showLoginDialog) {
        AlertDialog(
            onDismissRequest = { showLoginDialog = false },
            title = { Text("Yêu cầu đăng nhập") },
            text = { Text("Bạn cần đăng nhập để mượn sách. Bạn có muốn đăng nhập ngay bây giờ không?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLoginDialog = false
                        navController.navigate("login")
                    }
                ) {
                    Text("Đăng nhập")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLoginDialog = false }
                ) {
                    Text("Hủy")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CommonHeader(
                title = "Chi tiết sách",
                onBackClick = { navController.navigateUp() },
                onShareClick = { /* TODO: Implement share */ },
                showShareButton = false
            )
        }
    ) { paddingValues ->
        if (book != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Book Cover and Basic Info
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .background(Color(0xFFF5F5F5))
                ) {
                    AsyncImage(
                        model = book?.coverUrl,
                        contentDescription = "Book cover",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                // Book Details
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = book?.title ?: "",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tác giả: ${book?.author_name}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Thể loại: ${book?.category}",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Trạng thái: ${book?.status}",
                        fontSize = 16.sp,
                        color = when (book?.status) {
                            "Có sẵn" -> Color(0xFF4CAF50)
                            else -> Color(0xFFE53935)
                        },
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            if (isLoggedIn) {
                                navController.navigate("borrowBook/${book?.id}")
                            } else {
                                showLoginDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0288D1)
                        ),
                        enabled = book?.status == "Có sẵn"
                    ) {
                        Text(
                            text = if (book?.status == "Có sẵn") "Mượn sách" else "Không có sẵn",
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    book?.category?.let { category ->
                        RelatedBooksSection(category, navController)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    BookInfoSection(book?.description ?: "")
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun RelatedBooksSection(category: String, navController: NavController) {
    var relatedBooks by remember { mutableStateOf<List<Book>>(emptyList()) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(category) {
        scope.launch {
            relatedBooks = FirebaseManager.getBooksByCategory(category)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Sách cùng thể loại",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0288D1)
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(relatedBooks) { book ->
                RelatedBookItem(book, navController)
            }
        }
    }
}

@Composable
fun RelatedBookItem(book: Book, navController: NavController) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable {
                navController.navigate("bookDetails/${book.id}")
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = "Book cover for ${book.title}",
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
                    text = book.title,
                    maxLines = 1,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 12.sp
                )
                Text(
                    text = book.author_name,
                    maxLines = 1,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 10.sp,
                    modifier = Modifier.offset(y = (-2).dp)
                )
            }
        }
    }
}

@Composable
fun BookInfoSection(description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "GIỚI THIỆU SÁCH",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            fontSize = 18.sp,
            color = Color.DarkGray
        )
    }
}
