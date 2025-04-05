package com.example.librarybooklendingsystem.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BorrowedBookItem(book: Map<String, Any>) {
    val status = book["status"] as? String ?: "pending"
    val statusColor = when (status) {
        "pending" -> Color(0xFFFFA000) // Orange
        "approved" -> Color(0xFF4CAF50) // Green
        "returned" -> Color(0xFF2196F3) // Blue
        else -> Color.Gray
    }

    val statusText = when (status) {
        "pending" -> "Đang chờ"
        "approved" -> "Đã duyệt"
        "returned" -> "Đã trả"
        else -> "Không xác định"
    }

    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val borrowDate = (book["borrowDate"] as? Long)?.let { dateFormat.format(Date(it)) } ?: "N/A"
    val expectedReturnDate = (book["expectedReturnDate"] as? Long)?.let { dateFormat.format(Date(it)) } ?: "N/A"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Book cover
            AsyncImage(
                model = book["bookCover"] as? String,
                contentDescription = "Book cover",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Book info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = book["bookTitle"] as? String ?: "Không có tiêu đề",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Ngày mượn: $borrowDate",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Ngày trả dự kiến: $expectedReturnDate",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Status badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor
                )
            }
        }
    }
} 