package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.librarybooklendingsystem.data.Book
import com.example.librarybooklendingsystem.data.FirebaseManager
import com.example.librarybooklendingsystem.ui.components.CommonHeader
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailsScreen(
    navController: NavController,
    bookId: String
) {
    var book by remember { mutableStateOf<Book?>(null) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(bookId) {
        scope.launch {
            book = FirebaseManager.getBookById(bookId)
        }
    }

    if (showLoginDialog) {
        LoginRequiredDialog(
            onDismiss = { showLoginDialog = false },
            navController = navController
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CommonHeader(
            title = "Chi tiết sách",
            onBackClick = { navController.navigateUp() },
            onShareClick = { /* TODO: Share book */ }
        )

        if (book == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(30.dp))
                AsyncImage(
                    model = book?.coverUrl,
                    contentDescription = "Book Image",
                    modifier = Modifier
                        .width(200.dp)
                        .height(300.dp)
                        .padding(top = 16.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = book?.title ?: "",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Tác giả: ${book?.author ?: ""}", fontSize = 14.sp, color = Color.Black)
                    Text("Thể loại: ${book?.category ?: ""}", fontSize = 14.sp, color = Color.Black)
                    Text("Trạng thái sách: ${book?.status ?: ""}", fontSize = 14.sp, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            if (AuthState.isLoggedIn) {
                                navController.navigate("borrowbook/${book?.id}")
                            } else {
                                showLoginDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .height(50.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0093AB)
                        ),
                        border = BorderStroke(1.dp, Color.White)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Mượn sách", fontSize = 14.sp, color = Color.White)
                            Spacer(Modifier.width(6.dp))
                            Icon(
                                Icons.Outlined.FavoriteBorder,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                }

                book?.category?.let {
                    RelatedBooksSection(it, navController)
                }

                book?.description?.let {
                    BookInfoSection(it)
                }
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
            Text(
                text = book.title,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                fontSize = 12.sp
            )
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
