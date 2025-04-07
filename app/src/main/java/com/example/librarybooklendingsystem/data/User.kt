package com.example.librarybooklendingsystem.data

import java.util.Date

data class User(
    val id: String = "",
    val email: String = "",
    val role: String = "user",
    val createdAt: Date? = null,
    val isActive: Boolean = true
) {
    companion object {
        fun fromMap(id: String, data: Map<String, Any>): User {
            return User(
                id = id,
                email = data["email"] as? String ?: "",
                role = data["role"] as? String ?: "user",
                createdAt = data["createdAt"] as? Date,
                isActive = data["isActive"] as? Boolean ?: true
            )
        }
    }
} 