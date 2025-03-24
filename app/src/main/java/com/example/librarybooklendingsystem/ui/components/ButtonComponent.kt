package com.example.librarybooklendingsystem.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.Text
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
            .fillMaxWidth()
    ) {
        Text(text = text)
    }
}
