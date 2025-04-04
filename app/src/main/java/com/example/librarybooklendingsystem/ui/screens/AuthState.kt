package com.example.librarybooklendingsystem.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

object AuthState {
    var isLoggedIn by mutableStateOf(false)
} 