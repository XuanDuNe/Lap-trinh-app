package com.example.librarybooklendingsystem.data

import java.util.Date

data class Book(
    val id: String = "",
    val title: String = "",
    val author: String = "",
    val category: String = "",
    val status: String = "",
    val coverUrl: String = "",
    val description: String = "",
    val createdAt: Date = Date(),
    val borrowCount: Int = 0
) {
    companion object {
        fun fromMap(id: String, map: Map<String, Any>): Book {
            return Book(
                id = id,
                title = map["title"] as? String ?: "",
                author = map["author"] as? String ?: "",
                category = map["category"] as? String ?: "",
                status = map["status"] as? String ?: "",
                coverUrl = map["coverUrl"] as? String ?: "",
                description = map["description"] as? String ?: "",
                createdAt = (map["createdAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                borrowCount = (map["borrowCount"] as? Long)?.toInt() ?: 0
            )
        }

        fun toMap(book: Book): Map<String, Any> {
            return mapOf(
                "title" to book.title,
                "author" to book.author,
                "category" to book.category,
                "status" to book.status,
                "coverUrl" to book.coverUrl,
                "description" to book.description,
                "createdAt" to book.createdAt,
                "borrowCount" to book.borrowCount
            )
        }
    }
} 