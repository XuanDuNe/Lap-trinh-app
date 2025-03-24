package com.example.librarybooklendingsystem.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

@Composable
fun TextInput(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    label: String
) {
    Column {
        Text(text = label, fontSize = 16.sp)
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            textStyle = TextStyle(color = Color.Black, fontSize = 18.sp)
        )
    }
}
