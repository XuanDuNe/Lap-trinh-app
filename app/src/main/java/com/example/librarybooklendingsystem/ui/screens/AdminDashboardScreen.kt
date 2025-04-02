package com.example.librarybooklendingsystem.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.librarybooklendingsystem.R

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AdminDashboard {}
        }
    }
}

@Composable
fun AdminDashboard(onItemClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F6FC))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            backgroundColor = Color(0xFF0288D1),
            shape = RoundedCornerShape(8.dp),
            elevation = 5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Admin Avatar",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Admin",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Welcome Back!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(text = "Hi, Admin", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(20.dp))

        val statistics = listOf(
            "Thống kê mượn sách" to "Thống kê tình hình mượn sách theo thể loại",
            "Thống kê người dùng" to "Thống kê tình hình người dùng mượn sách",
            "Thống kê trả sách" to "Thống kê tình hình trả sách theo thể loại",
            "Thống kê sách trong thư viện" to "Thống kê tình hình sách theo thể loại",
            "Thống kê danh sách cần duyệt" to "Thống kê các yêu cầu đăng kí mượn sách"
        )

        statistics.forEach { (title, description) ->
            Card(
                modifier = Modifier
                    .height(130.dp)
                    .fillMaxWidth()
                    .padding(vertical = 15.dp)
                    .clickable { onItemClick(title) },
                shape = RoundedCornerShape(12.dp),
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD7E8FC)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic), // Thay bằng icon hợp lệ
                            contentDescription = null,
                            tint = Color(0xFF0288D1),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(text = description, fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboard() {
    AdminDashboard {}
}
