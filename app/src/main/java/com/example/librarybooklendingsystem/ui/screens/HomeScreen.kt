package com.example.librarybooklendingsystem.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.librarybooklendingsystem.R
import com.example.librarybooklendingsystem.data.Book
import com.example.librarybooklendingsystem.ui.viewmodels.BookViewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.librarybooklendingsystem.ui.components.CommonHeader
import com.example.librarybooklendingsystem.ui.viewmodels.BooksUiState
import kotlinx.coroutines.delay
import com.example.librarybooklendingsystem.ui.viewmodels.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: BookViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var currentBannerIndex by remember { mutableStateOf(0) }
    val selectedCategory by categoryViewModel.selectedCategory.collectAsState()
    val categoriesState by categoryViewModel.categories.collectAsState()
    
    // Danh sách các banner
    val banners = listOf(
        R.drawable.logouth,
        R.drawable.yeu,
        R.drawable.yeumbangmatgiuembangtim
    )

    // Tự động chuyển đổi banner mỗi 3 giây
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentBannerIndex = (currentBannerIndex + 1) % banners.size
        }
    }

    // Load books when category changes
    LaunchedEffect(selectedCategory) {
        Log.d("HomeScreen", "Selected category changed: $selectedCategory")
        if (selectedCategory != null) {
            val category = categoriesState.find { it.id == selectedCategory }
            category?.let {
                Log.d("HomeScreen", "Category found: ${it.name}, loading books...")
                viewModel.getBooksByCategory(it.name)
            }
        } else {
            Log.d("HomeScreen", "Selected category is null, loading all books...")
            viewModel.loadBooks()
        }
    }

    // Collect UI state
    val uiState by viewModel.uiState.collectAsState()

    // Filter books based on search query
    val filteredBooks = when (uiState) {
        is BooksUiState.Success -> {
            val books = (uiState as BooksUiState.Success).books
            if (searchQuery.isBlank()) {
                books
            } else {
                books.filter { book ->
                    book.title.contains(searchQuery, ignoreCase = true) ||
                    book.author_name.contains(searchQuery, ignoreCase = true) ||
                    book.category.contains(searchQuery, ignoreCase = true)
                }
            }
        }
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        CommonHeader(
            title = "My Library",
            onBackClick = { navController.navigateUp() },
            showShareButton = false
        )

        // Show selected category
        selectedCategory?.let { categoryId ->
            val category = categoriesState.find { it.id == categoryId }
            category?.let {
                Text(
                    text = "Thể loại: ${it.name}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            placeholder = { Text("Tìm kiếm sách...") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color(0xFF0093AB)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF0093AB),
                unfocusedBorderColor = Color.Gray,
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            when (uiState) {
                is BooksUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is BooksUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = (uiState as BooksUiState.Error).message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.loadBooks() }) {
                                Text("Thử lại")
                            }
                        }
                    }
                }
                is BooksUiState.Success -> {
                    val books = (uiState as BooksUiState.Success).books
                    if (books.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "Không có sách nào",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(onClick = { viewModel.loadBooks() }) {
                                    Text("Tải lại")
                                }
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Banner
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .padding(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.White
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = banners[currentBannerIndex]),
                                    contentDescription = "Banner",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // Top 10 đọc nhiều
                            SectionHeader(title = "Top 10 đọc nhiều")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredBooks.take(10)) { book ->
                                    FirebaseBookItem(
                                        navController = navController,
                                        book = book
                                    )
                                }
                            }

                            // Sách mới
                            SectionHeader(title = "Sách mới")
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(filteredBooks.sortedByDescending { it.createdAt }.take(20)) { book ->
                                    FirebaseBookItem(
                                        navController = navController,
                                        book = book
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun FirebaseBookItem(
    navController: NavController,
    book: Book
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(180.dp)
            .clickable {
                navController.navigate("bookDetails/${book.id}")
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = book.coverUrl,
                contentDescription = "Book Cover",
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}

@Preview(showBackground = true)
@Composable
fun FirebaseBookItemPreview() {
    val book = Book(
        id = "1",
        title = "Sample Book",
        author_name = "Author",
        category = "Category",
        status = "Có sẵn",
        coverUrl = "",
        description = "",
        createdAt = java.util.Date(),
        borrowCount = 0
    )
    FirebaseBookItem(
        navController = rememberNavController(),
        book = book
    )
}

@Preview(showBackground = true)
@Composable
fun SectionHeaderPreview() {
    SectionHeader(title = "Top 10 đọc nhiều")
}



