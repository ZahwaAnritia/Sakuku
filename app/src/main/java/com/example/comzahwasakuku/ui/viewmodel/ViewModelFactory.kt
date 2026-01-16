package com.example.comzahwasakuku.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.comzahwasakuku.data.local.AppDatabase
import com.example.comzahwasakuku.data.repository.AuthRepository
import com.example.comzahwasakuku.data.repository.CategoryRepository
import com.example.comzahwasakuku.data.repository.TransactionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // (untuk isi kategori otomatis)
        val database = AppDatabase.getDatabase(context, CoroutineScope(Dispatchers.IO))


        // Ini agar Login bisa mengambil data Limit dari Database
        val authRepository = AuthRepository(context, database.userDao())

        return when {

            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {

                AuthViewModel(authRepository) as T
            }


            modelClass.isAssignableFrom(DashboardViewModel::class.java) -> {

                DashboardViewModel(authRepository, database.transactionDao()) as T
            }


            modelClass.isAssignableFrom(TransactionViewModel::class.java) -> {
                val repo = TransactionRepository(database.transactionDao())
                TransactionViewModel(repo) as T
            }


            modelClass.isAssignableFrom(CategoryViewModel::class.java) -> {
                val repo = CategoryRepository(database.categoryDao())
                CategoryViewModel(repo) as T
            }


            modelClass.isAssignableFrom(LaporanViewModel::class.java) -> {
                val repo = TransactionRepository(database.transactionDao())
                LaporanViewModel(repo) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }
}