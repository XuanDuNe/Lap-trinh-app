package com.example.librarybooklendingsystem.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ButtonComponent(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier
    ) {
        Text(text = text)
    }
}

