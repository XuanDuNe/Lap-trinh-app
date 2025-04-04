package com.example.librarybooklendingsystem.ui.screens

import com.google.firebase.auth.FirebaseAuth

object AuthState {
    private val auth = FirebaseAuth.getInstance()
    
    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun getCurrentUser() = auth.currentUser
} 