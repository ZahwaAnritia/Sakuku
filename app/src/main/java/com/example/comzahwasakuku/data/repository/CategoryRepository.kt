package com.example.comzahwasakuku.data.repository

import com.example.comzahwasakuku.data.local.dao.CategoryDao
import com.example.comzahwasakuku.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    // --- REVISI: Menggunakan User ID (Int), bukan Email ---
    fun getAllCategories(userId: Int): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories(userId)
    }

    // Fungsi untuk menambah kategori baru secara lokal
    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }

    // Fungsi untuk mengisi data awal (opsional)
    suspend fun insertAll(categories: List<CategoryEntity>) {
        categoryDao.insertAll(categories)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }
}