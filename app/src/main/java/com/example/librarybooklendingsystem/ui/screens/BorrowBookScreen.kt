package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
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

@Composable
fun BorrowBookScreen(navController: NavController) {
    val studentName = remember { mutableStateOf(TextFieldValue("")) }
    val expectedReturnDate = remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Mượn sách",
                            fontSize = 28.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = " Back ",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Filled.Share, contentDescription = "Chia sẻ",
                            modifier = Modifier.size(35.dp))
                    }
                },
                backgroundColor = Color(0xFF0B8FAC),
                contentColor = Color.White,
                modifier = Modifier.height(100.dp)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ảnh sách
                Image(
                    painter = painterResource(id = R.drawable.yeumbangmatgiuembangtim),
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
                    text = "Yêu Em Bằng Mắt Giữ Em Bằng Tim",
                    style = MaterialTheme.typography.subtitle1,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

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

                // Ô nhập ngày
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

                // xác nhận
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp)
                ) {
                    Text(text = "Xác nhận", fontSize = 20.sp)
                }
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
fun BorrowBookScreenPreview() {
    BorrowBookScreen(navController = rememberNavController())
}