package com.example.librarybooklendingsystem.data

import com.google.firebase.Timestamp
import java.util.Date

data class Borrow(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "", // Thêm email để tiện hiển thị nếu cần
    val bookId: String = "",
    val bookTitle: String = "",
    val bookCategory: String = "", // Trường bạn đang dùng
    val borrowDate: Date? = null,
    val expectedReturnDate: Date? = null,
    val actualReturnDate: Date? = null,
    val status: String = "" // Trường bạn đang dùng ("Đang mượn", "Đã trả", ...)
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any>): Borrow {
            return Borrow(
                id = id,
                userId = map["userId"] as? String ?: "",
                userEmail = map["userEmail"] as? String ?: "",
                bookId = map["bookId"] as? String ?: "",
                bookTitle = map["bookTitle"] as? String ?: "",
                bookCategory = map["bookCategory"] as? String ?: "",
                borrowDate = (map["borrowDate"] as? Timestamp)?.toDate(),
                expectedReturnDate = (map["expectedReturnDate"] as? Timestamp)?.toDate(),
                actualReturnDate = (map["actualReturnDate"] as? Timestamp)?.toDate(),
                status = map["status"] as? String ?: ""
            )
        }
    }
} 