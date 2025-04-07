package com.example.librarybooklendingsystem.data

import java.util.Date

data class Book(
    val id: String = "",
    val title: String = "",
    val author_id: String = "",
    val author_name: String = "",
    val category: String = "",
    val status: String = "",
    val coverUrl: String = "",
    val description: String = "",
    val quantity: Int = 0,
    val availableCopies: String = "0",
    val createdAt: Date = Date(),
    val borrowCount: Int = 0
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any>): Book {
            return Book(
                id = id,
                title = map["title"] as? String ?: "",
                author_id = map["author_id"] as? String ?: "",
                author_name = map["author_name"] as? String ?: "",
                category = map["category"] as? String ?: "",
                status = map["status"] as? String ?: "",
                coverUrl = map["coverUrl"] as? String ?: "",
                description = map["description"] as? String ?: "",
                quantity = (map["quantity"] as? Long)?.toInt() ?: 0,
                availableCopies = map["availableCopies"] as? String ?: "0",
                createdAt = (map["createdAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                borrowCount = (map["borrowCount"] as? Long)?.toInt() ?: 0
            )
        }

        fun toMap(book: Book): Map<String, Any> {
            return mapOf(
                "title" to book.title,
                "author_id" to book.author_id,
                "author_name" to book.author_name,
                "category" to book.category,
                "status" to book.status,
                "coverUrl" to book.coverUrl,
                "description" to book.description,
                "quantity" to book.quantity,
                "availableCopies" to book.availableCopies,
                "createdAt" to book.createdAt,
                "borrowCount" to book.borrowCount
            )
        }
    }
} 