package com.example.librarybooklendingsystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.librarybooklendingsystem.ui.screens.BorrowBookScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BorrowBookScreen()
        }
    }
}
<<<<<<< HEAD
=======

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

}
>>>>>>> 374c82a33888f41e851474f594a4802da0533454
