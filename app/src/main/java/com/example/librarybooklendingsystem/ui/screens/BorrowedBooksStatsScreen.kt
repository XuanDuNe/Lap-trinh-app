package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.librarybooklendingsystem.ui.components.ButtonComponent
import com.example.librarybooklendingsystem.ui.components.TextInput


@Composable
fun BorrowBookScreen() {
    val studentName = remember { mutableStateOf(TextFieldValue("")) }
    val expectedReturnDate = remember { mutableStateOf(TextFieldValue("")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Mượn sách",
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Hình ảnh sách
        Image(
            painter = painterResource(id = R.drawable.yeumbangmatgiuembangtim),
            contentDescription = "Book Image",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Yêu Em Bằng Mắt Giữ Em Bằng Tim", // Tên sách
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Input học và tên
        TextInput(
            value = studentName.value,
            onValueChange = { studentName.value = it },
            label = "Nhập học và tên"
        )

        // Input ngày dự kiến trả
        TextInput(
            value = expectedReturnDate.value,
            onValueChange = { expectedReturnDate.value = it },
            label = "Ngày dự kiến trả"
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nút xác nhận
        ButtonComponent(
            onClick = { /* Xử lý xác nhận mượn sách */ },
            text = "Xác nhận"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BorrowBookScreenPreview() {
    BorrowBookScreen()
}
