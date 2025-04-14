package com.example.librarybooklendingsystem.ui.navigation

import android.util.Log
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.librarybooklendingsystem.ui.screens.*
import com.example.librarybooklendingsystem.ui.viewmodels.BookViewModel
import com.example.librarybooklendingsystem.ui.viewmodels.CategoryViewModel
import com.example.librarybooklendingsystem.data.AuthState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isAdmin by AuthState.isAdmin.collectAsStateWithLifecycle()
    val userRole by AuthState.currentUserRole.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val categoryViewModel = viewModel<CategoryViewModel>()
    val bookViewModel = viewModel<BookViewModel>()
    val isLoggedIn by AuthState.isLoggedIn.collectAsStateWithLifecycle()

    // Xác định màn hình bắt đầu dựa vào role
    val startDestination = if (isAdmin && userRole == "admin") {
        "admin_dashboard"
    } else {
        "home"
    }

    // Danh sách các màn hình được phép hiển thị drawer
    val allowedRoutes = listOf("home", "bookDetails", "borrowbook", "account")

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (currentRoute in allowedRoutes) {
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
                MainContent(
                    navController = navController,
                    currentRoute = currentRoute,
                    isAdmin = isAdmin,
                    userRole = userRole,
                    isLoggedIn = isLoggedIn,
                    startDestination = startDestination,
                    bookViewModel = bookViewModel,
                    categoryViewModel = categoryViewModel,
                    drawerState = drawerState,
                    scope = scope
                )
            }
        } else {
            MainContent(
                navController = navController,
                currentRoute = currentRoute,
                isAdmin = isAdmin,
                userRole = userRole,
                isLoggedIn = isLoggedIn,
                startDestination = startDestination,
                bookViewModel = bookViewModel,
                categoryViewModel = categoryViewModel,
                drawerState = drawerState,
                scope = scope
            )
        }
    }
}

@Composable
private fun MainContent(
    navController: NavHostController,
    currentRoute: String?,
    isAdmin: Boolean,
    userRole: String?,
    isLoggedIn: Boolean,
    startDestination: String,
    bookViewModel: BookViewModel,
    categoryViewModel: CategoryViewModel,
    drawerState: DrawerState,
    scope: CoroutineScope
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentColor = MaterialTheme.colorScheme.onBackground,
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.navigationBars,
        bottomBar = {
            if (currentRoute != "login" && 
                currentRoute != "signup" && 
                !isAdmin && 
                userRole != "admin") {
                Surface(
                    tonalElevation = 3.dp,
                    shadowElevation = 3.dp
                ) {
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
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Màn hình chính
            composable(
                "home",
                enterTransition = { fadeIn() },
                exitTransition = { fadeOut() }
            ) { 
                if (isAdmin && userRole == "admin") {
                    LaunchedEffect(Unit) {
                        navController.navigate("admin_dashboard") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                } else {
                    HomeScreen(navController, bookViewModel, categoryViewModel) 
                }
            }

            // Màn hình đăng nhập và đăng ký
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

            // Màn hình chi tiết sách và mượn sách
            composable(
                route = "bookDetails/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
                    BookDetailsScreen(navController = navController, bookId = bookId)
                }
            }

            composable(
                route = "borrowbook/{bookId}",
                arguments = listOf(
                    navArgument("bookId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    val bookId = backStackEntry.arguments?.getString("bookId")
                    BorrowBookScreen(navController = navController, bookId = bookId)
                }
            }

            composable("borrowbook") {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    BorrowBookScreen(navController = navController)
                }
            }

            // Màn hình tài khoản và cài đặt
            composable("account") { 
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    AccountScreen(navController) 
                }
            }
            // Màn hình admin
            composable("admin_dashboard") {
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else if (isAdmin && userRole == "admin") {
                    AdminDashboardScreen(navController)
                } else {
                    LaunchedEffect(Unit) {
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            }

            // Màn hình thống kê
            composable("library_stats") { 
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    LibraryStatsScreen(navController) 
                }
            }
            
            composable("user_stats") { 
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    UserStatsScreen(navController) 
                }
            }
            
            composable("borrowed_books_stats") { 
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    BorrowedBooksStatsScreen(navController) 
                }
            }
            
            composable("returned_books_stats") { 
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    ReturnedBooksStatsScreen(navController) 
                }
            }
            
            composable("pending_books_approval") { 
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    PendingBooksApprovalScreen(navController) 
                }
            }

            composable("pending_books_return") { 
                if (!isLoggedIn) {
                    LaunchedEffect(Unit) {
                        navController.navigate("login")
                    }
                } else {
                    PendingBooksReturnScreen(navController) 
                }
            }
        }
    }
} 