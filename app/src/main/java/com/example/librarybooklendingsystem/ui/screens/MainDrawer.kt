package com.example.librarybooklendingsystem.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.librarybooklendingsystem.data.AuthState
import com.example.librarybooklendingsystem.data.FirebaseManager
import com.example.librarybooklendingsystem.ui.viewmodels.CategoryViewModel
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.launch
import androidx.lifecycle.compose.collectAsStateWithLifecycle

data class Category(
    val id: String,
    val name: String
)

val categories = listOf(
    Category("Tiểu thuyết", "Tiểu thuyết"),
    Category("Văn học", "Văn học"),
    Category("Văn học thiếu nhi", "Văn học thiếu nhi"),
    Category("Khoa học viễn tưởng", "Khoa học viễn tưởng"),
    Category("Kỹ năng sống", "Kỹ năng sống"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainDrawer(
    navController: NavController,
    onCloseDrawer: () -> Unit,
    categoryViewModel: CategoryViewModel
) {
    val isLoggedIn by AuthState.isLoggedIn.collectAsStateWithLifecycle()
    val currentUser by AuthState.currentUser.collectAsState(initial = null)
    val selectedCategory by categoryViewModel.selectedCategory.collectAsState()

    var userName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            scope.launch {
                try {
                    val userInfo = FirebaseManager.getUserInfo(currentUser!!.uid)
                    userName = userInfo?.get("name") as? String ?: ""
                    Log.d("MainDrawer", "User info: $userInfo")
                    Log.d("MainDrawer", "User name: $userName")
                } catch (e: Exception) {
                    Log.e("MainDrawer", "Error getting user info: ${e.message}")
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(Color.White)
    ) {
        // Header with organization name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Organization Icon",
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF4285F4)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (userName.isNotEmpty()) userName else "Người dùng",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Thư viện sách",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // Book Categories Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "Thể loại sách",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            categories.forEach { category ->
                DrawerMenuItem(
                    icon = Icons.Default.AccountBox,
                    text = category.name,
                    selected = selectedCategory == category.id,
                    onClick = {
                        Log.d("MainDrawer", "Category selected - ID: ${category.id}, Name: ${category.name}")
                        categoryViewModel.setSelectedCategory(category.id)
                        onCloseDrawer()
                    }
                )
            }
        }

        Spacer(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    text: String,
    selected: Boolean = false,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = if (selected) Color(0xFF4285F4) else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                color = if (selected) Color(0xFF4285F4) else Color.Black,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
} 