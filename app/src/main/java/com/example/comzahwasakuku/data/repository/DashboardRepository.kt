package com.example.comzahwasakuku.data.repository

import com.example.comzahwasakuku.data.local.UserPreferences
import com.example.comzahwasakuku.data.local.dao.TransactionDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DashboardRepository(
    private val transactionDao: TransactionDao,
    private val userPreferences: UserPreferences
) {

    // Kita pakai .map { it ?: "User" } artinya:
    // "Kalau namanya null (kosong), ganti jadi tulisan 'User'"
    val userName: Flow<String> = userPreferences.userName.map { it ?: "User" }


    fun getTotalExpense(userId: Int): Flow<Double> {
        return transactionDao.getTotalExpense(userId).map { it ?: 0.0 }
    }


    fun getTotalIncome(userId: Int): Flow<Double> {
        return transactionDao.getTotalIncome(userId).map { it ?: 0.0 }
    }
}