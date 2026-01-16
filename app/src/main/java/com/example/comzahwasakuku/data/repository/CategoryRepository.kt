package com.example.comzahwasakuku.data.repository

import com.example.comzahwasakuku.data.local.dao.CategoryDao
import com.example.comzahwasakuku.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {


    fun getAllCategories(userId: Int): Flow<List<CategoryEntity>> {
        return categoryDao.getAllCategories(userId)
    }


    suspend fun insertCategory(category: CategoryEntity) {
        categoryDao.insertCategory(category)
    }


    suspend fun insertAll(categories: List<CategoryEntity>) {
        categoryDao.insertAll(categories)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }
}