package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.librarybooklendingsystem.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController, startDestination = "borrow") {
                composable("home") { AccountScreen(navController) }
                composable("borrow") { BorrowBookScreen(navController) }
                composable("account") { AccountScreen(navController) }
            }
        }
    }
}

@Composable
fun AccountScreen(navController: NavController?) {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            navController?.navigate("login") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    if (currentUser == null) {
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cá Nhân", fontSize = 28.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = { navController?.navigateUp() }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Back", modifier = Modifier.size(50.dp))
                    }
                },
                actions = {
                    IconButton(onClick = { /* Hành động chia sẻ */ }) {
                        Icon(Icons.Filled.Share, contentDescription = "Chia sẻ", modifier = Modifier.size(35.dp))
                    }
                },
                backgroundColor = Color(0xFF0093AB),
                contentColor = Color.White,
                modifier = Modifier.height(100.dp)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img),
                    contentDescription = "Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(currentUser.email ?: "Người dùng", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("ID: ${currentUser.uid}", fontSize = 16.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { 
                        auth.signOut()
                        navController?.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .width(120.dp)
                        .height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0093AB))
                ) {
                    Text("Đăng xuất", color = Color.White)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sách đang mượn", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                BookGrid()
            }
        },
    )
}

@Composable
fun BookGrid() {
    val books = listOf(
        Book(
            "Yêu em bằng mắt giữ em bằng tim",
            "Nguyễn Văn Duy",
            "Ngày mượn: 10/10/2025",
            "Ngày dự kiến trả: 12/12/2025",
            "Ngôn tình",
            "Có sẵn",
            R.drawable.yeumbangmatgiuembangtim,
            "123"
        ),
        Book(
            "Yêu",
            "Nguyễn Văn Duy",
            "Ngày mượn: 10/10/2025",
            "Ngày dự kiến trả: 15/12/2025",
            "Ngôn tình",
            "Có sẵn",
            R.drawable.yeumbangmatgiuembangtim,
            "123"
        ),
        Book(
            "Lì quá để nói quài",
            "Nguyễn Văn Duy",
            "Ngày mượn: 10/10/2025",
            "Ngày dự kiến trả: 20/12/2025",
            "Ngôn tình",
            "Có sẵn",
            R.drawable.yeumbangmatgiuembangtim,
            "123"
        ),
        Book(
            "Yêu em bằng mắt giữ em bằng tim",
            "Nguyễn Văn Duy",
            "Ngày mượn: 10/10/2025",
            "Ngày dự kiến trả: 12/12/2025",
            "Ngôn tình",
            "Có sẵn",
            R.drawable.yeumbangmatgiuembangtim,
            "123"
        ),
        Book(
            "Yêu",
            "Nguyễn Văn Duy",
            "Ngày mượn: 10/10/2025",
            "Ngày dự kiến trả: 15/12/2025",
            "Ngôn tình",
            "Có sẵn",
            R.drawable.yeumbangmatgiuembangtim,
            "123"
        ),
        Book(
            "Lì quá để nói quài",
            "Nguyễn Văn Duy",
            "Ngày mượn: 10/10/2025",
            "Ngày dự kiến trả: 20/12/2025",
            "Ngôn tình",
            "Có sẵn",
            R.drawable.yeumbangmatgiuembangtim,
            "123"
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(books) { book -> BookItem(book) }
    }
}

@Composable
fun BookItem(book: Book) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.yeumbangmatgiuembangtim),
            contentDescription = "Book Image",
            modifier = Modifier
                .width(110.dp)
                .height(120.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(book.title, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Text(book.author, fontSize = 12.sp, color = Color.Gray)
        Text(book.borrowDate, fontSize = 12.sp, color = Color.Gray)
        Text(book.dueDate, fontSize = 12.sp, color = Color.Gray)
    }
}

data class Book(
    val title: String,
    val author: String,
    val borrowDate: String,
    val dueDate: String,
    val genre: String,
    val status: String,
    val coverResId: Int,
    val id: String
)

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    AccountScreen(navController = null)
}

