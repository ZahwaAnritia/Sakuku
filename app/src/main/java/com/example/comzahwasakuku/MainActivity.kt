package com.example.comzahwasakuku

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.comzahwasakuku.data.local.AppDatabase
import com.example.comzahwasakuku.data.repository.AuthRepository
import com.example.comzahwasakuku.ui.navigation.AppNavigation
import com.example.comzahwasakuku.ui.theme.ComZahwaSakukuTheme
import com.example.comzahwasakuku.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- 1. SETUP DATABASE ---
        // Scope ini digunakan untuk callback pengisian data kategori default (SYSTEM)
        val database = AppDatabase.getDatabase(applicationContext, CoroutineScope(Dispatchers.IO))

        // --- 2. SETUP AUTH REPOSITORY ---
        // AuthRepository membutuhkan Context (untuk DataStore) dan UserDao (untuk Database)
        val authRepository = AuthRepository(applicationContext, database.userDao())

        // --- 3. SETUP VIEWMODEL FACTORY ---
        // Factory ini bertugas membuat ViewModel (Auth, Transaction, Category, dll)
        val viewModelFactory = ViewModelFactory(applicationContext)

        setContent {
            ComZahwaSakukuTheme {
                val navController = rememberNavController()

                // Jalankan Sistem Navigasi Utama
                AppNavigation(
                    navController = navController,
                    viewModelFactory = viewModelFactory,
                    authRepository = authRepository
                )
            }
        }
    }
}