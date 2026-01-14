package com.example.comzahwasakuku.ui.navigation


import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.comzahwasakuku.data.repository.AuthRepository
import com.example.comzahwasakuku.ui.components.BottomNavigationBar
import com.example.comzahwasakuku.ui.screens.*
import com.example.comzahwasakuku.ui.viewmodel.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppNavigation(
    navController: NavHostController,
    viewModelFactory: ViewModelFactory,
    authRepository: AuthRepository
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val scope = rememberCoroutineScope()

    val showBottomBar = currentRoute in listOf("dashboard", "profile", "laporan")
    val showFab = currentRoute == "dashboard"

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    navController = navController,
                    onFabClick = { navController.navigate("add_transaction") }
                )
            }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { navController.navigate("add_transaction") },
                    containerColor = com.example.comzahwasakuku.ui.theme.CyanPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.size(65.dp).padding(4.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Tambah", modifier = Modifier.size(30.dp))
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier
                .padding(innerPadding)
                .background(com.example.comzahwasakuku.ui.theme.CyanPrimary)
        ) {

            // 1. SPLASH
            composable("splash") {
                SplashScreen(onTimeout = {
                    scope.launch {
                        val user = authRepository.userSession.first()
                        if (user != null) {
                            navController.navigate("dashboard") { popUpTo("splash") { inclusive = true } }
                        } else {
                            navController.navigate("login") { popUpTo("splash") { inclusive = true } }
                        }
                    }
                })
            }

            // 2. LOGIN
            composable("login") {
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = { navController.navigate("dashboard") { popUpTo("login") { inclusive = true } } },
                    onNavigateToRegister = { navController.navigate("register") }
                )
            }

            // 3. REGISTER
            composable("register") {
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                RegisterScreen(
                    viewModel = authViewModel,
                    onRegisterSuccess = { navController.navigate("login") { popUpTo("register") { inclusive = true } } },
                    onNavigateToLogin = { navController.popBackStack() }
                )
            }

            // 4. DASHBOARD
            composable(
                route = "dashboard",
                enterTransition = { fadeIn(animationSpec = tween(1000)) }
            ) {
                val dashboardViewModel: DashboardViewModel = viewModel(factory = viewModelFactory)
                DashboardScreen(navController = navController, viewModel = dashboardViewModel)
            }

            // 5. PROFILE
            composable("profile") {
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                ProfileScreen(
                    viewModel = authViewModel,
                    onBackClick = { navController.popBackStack() },
                    onCategoryClick = { navController.navigate("categories") },
                    onLogoutClick = {
                        scope.launch {
                            authRepository.logout()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    }
                )
            }

            // 6. KATEGORI
            composable("categories") {
                val categoryViewModel: CategoryViewModel = viewModel(factory = viewModelFactory)
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory) // Tambah ini

                CategoryScreen(
                    viewModel = categoryViewModel,
                    authViewModel = authViewModel, // Kirim ke sini
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 7. LAPORAN (PERBAIKAN: TAMBAH AUTH VIEWMODEL)
            composable("laporan") {
                val laporanViewModel: LaporanViewModel = viewModel(factory = viewModelFactory)
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory) // <--- BUAT INI

                LaporanScreen(
                    navController = navController,
                    viewModel = laporanViewModel,
                    authViewModel = authViewModel // <--- KIRIM KE SCREEN
                )
            }

            // 8. ADD TRANSACTION (PERBAIKAN: TAMBAH AUTH VIEWMODEL)
            composable("add_transaction") {
                val transactionViewModel: TransactionViewModel = viewModel(factory = viewModelFactory)
                val categoryViewModel: CategoryViewModel = viewModel(factory = viewModelFactory)
                val authViewModel: AuthViewModel = viewModel(factory = viewModelFactory) // <--- BUAT INI

                AddTransactionScreen(
                    navController = navController,
                    transactionViewModel = transactionViewModel,
                    categoryViewModel = categoryViewModel,
                    authViewModel = authViewModel // <--- KIRIM KE SCREEN
                )
            }
        }
    }
}