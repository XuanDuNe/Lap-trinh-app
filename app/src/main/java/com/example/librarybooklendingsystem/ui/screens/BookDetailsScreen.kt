package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import com.example.librarybooklendingsystem.R
@Composable
fun BookDetailsScreen(navController: NavController) {
    var showLoginDialog by remember { mutableStateOf(false) }

    if (showLoginDialog) {
        LoginRequiredDialog(
            onDismiss = { showLoginDialog = false },
            navController = navController
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(100.dp),
                title = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Chi tiết sách",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                backgroundColor = Color(0xFF0288D1),
                contentColor = Color.White,
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = " Back ",
                            modifier = Modifier.size(50.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Chia sẻ */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Chia sẻ")
                    }
                }
            )
        },
        backgroundColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))
            Image(
                painter = painterResource(id = R.drawable.yeumbangmatgiuembangtim),
                contentDescription = "Book Image",
                modifier = Modifier
                    .size(220.dp)
                    .padding(top = 16.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Yêu Em Bằng Mắt Giữ Em Bằng Tim",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Tác giả: Xuân Dự", fontSize = 14.sp, color = Color.Black)
                Text("Thể loại: Ngôn tình", fontSize = 14.sp, color = Color.Black)
                Text("Trạng thái sách: Có sẵn", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { 
                        if (AuthState.isLoggedIn) {
                            navController.navigate("borrowbook")
                        } else {
                            showLoginDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(50.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF0288D1)),
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

            RelatedBooksSection()
            BookInfoSection()
        }
    }
}
@Composable
fun RelatedBooksSection() {
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
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(listOf(
                R.drawable.yeumbangmatgiuembangtim to "Yêu em bằng mắt\ngiữ em bằng tim",
                R.drawable.yeu to "Yêu",
                R.drawable.li to "Lì quá để\nnói quài"
            )) { (image, title) ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(120.dp)
                ) {
                    Image(
                        painter = painterResource(id = image),
                        contentDescription = title,
                        modifier = Modifier
                            .height(150.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun BookInfoSection() {
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
            text = "Phương đã từng có một tình yêu trong trẻo tinh khôi ở tuổi 18 với Trung - người trợ lý tài giỏi, trung thành của bà nội. Nhưng họ sớm bị chia cắt khi Phương qua Pháp đoàn tụ với mẹ, và mất liên lạc trong 10 năm ròng rã. Định mệnh cuối cùng cũng cho họ gặp nhau giải tỏa những hiểu lầm và nối lại mối duyên xưa. Nhưng thách thức vẫn chưa hết. Một lần nữa, Phương và Trung phải đứng trước lựa chọn nắm tay hoặc buông nhau ra...",
            fontSize = 18.sp,
            color = Color.DarkGray
        )
        Text(
            text= "Truyện diễn ra với bối cảnh Pháp và Việt Nam, với những phân đoạn tả cảnh tả tình lãng mạn bay bổng, những phút bên nhau đầy say đắm, nhưng cũng không thiếu những gai góc có thể chia rẽ mọi đôi tình nhân - cho dù họ đã có một khởi đầu đẹp đẽ như thế nào chăng nữa. Vì yêu nhau rõ ràng là từ ánh mắt, nhưng muốn ở bên nhau trọn đời, cần phải có một con tim rộng mở, bao dung...",
            fontSize = 18.sp,
            color = Color.DarkGray
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "THÔNG TIN CHI TIẾT",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        val details = listOf(
            "Tác giả: Dương Thụy",
            "Nhà xuất bản: NXB Trẻ",
            "Nhà phát hành: NXB Trẻ",
            "Mã sản phẩm: 8934974173663",
            "Khối lượng: 580.00 gam",
            "Ngôn ngữ: Tiếng Việt",
            "Định dạng: Bìa mềm",
            "Kích thước: 20 x 13 cm",
            "Ngày phát hành: 2021",
            "Số trang: 480"
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
        ) {
            details.forEach { detail ->
                Text(
                    text = "• $detail",
                    fontSize = 18.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}