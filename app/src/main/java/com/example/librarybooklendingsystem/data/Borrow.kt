package com.example.librarybooklendingsystem.data

import com.google.firebase.Timestamp
import java.util.Date

data class Borrow(
    val id: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val bookId: String = "",
    val bookTitle: String = "",
    val bookCategory: String = "",
    val borrowDate: Date? = null,
    val expectedReturnDate: Date? = null,
    val actualReturnDate: Date? = null,
    val status: String = ""
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