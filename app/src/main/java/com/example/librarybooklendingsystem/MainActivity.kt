package com.example.librarybooklendingsystem
import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.librarybooklendingsystem.ui.screens.*
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import com.example.librarybooklendingsystem.data.AuthState
import com.example.librarybooklendingsystem.ui.theme.LibraryBookLendingSystemTheme
import kotlin.math.sign
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.ModalDrawerSheet
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.librarybooklendingsystem.ui.navigation.AppNavigation
import com.example.librarybooklendingsystem.ui.viewmodels.BookViewModel
import com.example.librarybooklendingsystem.ui.viewmodels.CategoryViewModel

class MainActivity : ComponentActivity() {
    private lateinit var analytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        analytics = Firebase.analytics
        analytics.setAnalyticsCollectionEnabled(true)

        // Initialize Firebase
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("FirebaseCheck", "Firebase được khởi tạo trong MainActivity")
            }
            
            val db = FirebaseFirestore.getInstance()
            Log.d("FirebaseCheck", "Firestore instance created successfully")
            
            // Test connection
            db.collection("books").get()
                .addOnSuccessListener { 
                    Log.d("FirebaseCheck", "Kết nối Firebase thành công! Số lượng sách: ${it.size()}")
                }
                .addOnFailureListener { e ->
                    Log.e("FirebaseCheck", "Lỗi kết nối Firebase: ${e.message}")
                }
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "Lỗi khởi tạo Firebase: ${e.message}")
            e.printStackTrace()
        }

        // Initialize AuthState
        AuthState.init(this)

        setContent {
            LibraryBookLendingSystemTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("FirebaseCheck", "Firebase được khởi tạo trong Application")
            }
        } catch (e: Exception) {
            Log.e("FirebaseCheck", "Lỗi khởi tạo Firebase trong Application: ${e.message}")
            e.printStackTrace()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isAdmin by AuthState.isAdmin.collectAsStateWithLifecycle()
    val userRole by AuthState.currentUserRole.collectAsStateWithLifecycle()
    val isLoggedIn by AuthState.isLoggedIn.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val bookViewModel: BookViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()

    LaunchedEffect(Unit) {
        if (isLoggedIn && (currentRoute == "login" || currentRoute == "signup")) {
            if (isAdmin && userRole == "admin") {
                navController.navigate("admin_dashboard") {
                    popUpTo(0) { inclusive = true }
                }
            } else {
                navController.navigate("home") {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                MainDrawer(
                    navController = navController,
                    onCloseDrawer = {
                        scope.launch {
                            drawerState.close()
                        }
                    },
                    categoryViewModel = categoryViewModel
                )
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (currentRoute != "login" && currentRoute != "signup") {
                    BottomNavigationBar(
                        navController = navController,
                        onOpenDrawer = {
                            scope.launch {
                                drawerState.open()
                            }
                        },
                        categoryViewModel = categoryViewModel
                    )
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(
                    "login",
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) { LoginScreen(navController) }
                
                composable(
                    "signup",
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) { SignUpScreen(navController) }
                
                composable(
                    "home",
                    enterTransition = { fadeIn() },
                    exitTransition = { fadeOut() }
                ) { 
                    HomeScreen(
                        navController = navController,
                        viewModel = bookViewModel,
                        categoryViewModel = categoryViewModel
                    )
                }

                composable(
                    route = "bookDetails/{bookId}",
                    arguments = listOf(
                        navArgument("bookId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId")
                    if (bookId != null) {
                        BookDetailsScreen(navController, bookId)
                    }
                }
                composable(
                    route = "borrowbook/{bookId}",
                    arguments = listOf(
                        navArgument("bookId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val bookId = backStackEntry.arguments?.getString("bookId")
                    BorrowBookScreen(navController = navController, bookId = bookId)
                }
                composable("borrowbook") { 
                    BorrowBookScreen(navController = navController) 
                }
                composable("account") { AccountScreen(navController) }
                composable("admin_dashboard") { 
                    if (isAdmin && userRole == "admin") {
                        AdminDashboardScreen(navController)
                    } else {
                        LaunchedEffect(Unit) {
                            navController.navigate("home") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                }
                composable("create_admin") { CreateAdminScreen(navController) }
                composable("pending_approvals") { PendingBooksApprovalScreen(navController) }
                composable("library_stats") { LibraryStatsScreen(navController) }
                composable("user_stats") { UserStatsScreen(navController) }
                composable("borrowed_books_stats") { BorrowedBooksStatsScreen(navController) }
                composable("returned_books_stats") { ReturnedBooksStatsScreen(navController) }
            }
        }
    }
}
