package com.example.comzahwasakuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comzahwasakuku.data.local.dao.TransactionDao
import com.example.comzahwasakuku.data.repository.AuthRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModel(
    private val repository: AuthRepository,
    private val transactionDao: TransactionDao
) : ViewModel() {

    // HELPER: HITUNG JAM (Untuk filter hari ini)
    private val startOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            return calendar.timeInMillis
        }

    private val endOfDay: Long
        get() {
            val calendar = Calendar.getInstance()
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            return calendar.timeInMillis
        }

    // 1. Nama User
    val userName: StateFlow<String> = repository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Mahasiswa")

    // 2. Limit Bulanan
    val dailyLimit: StateFlow<Double> = repository.currentLimit
        .map { it.toDoubleOrNull() ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // 3. PENGELUARAN HARI INI

    val todayExpense: StateFlow<Double> = repository.userId.flatMapLatest { id ->
        transactionDao.getExpenseByDate(id, startOfDay, endOfDay)
    }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // 4. TOTAL PENGELUARAN
    val totalExpense: StateFlow<Double> = repository.userId.flatMapLatest { id ->
        transactionDao.getTotalExpense(id)
    }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)


    // 6. Pemasukan (FIX: Pakai userId)
    val totalIncome: StateFlow<Double> = repository.userId.flatMapLatest { id ->
        transactionDao.getTotalIncome(id)
    }
        .map { it ?: 0.0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    // 5. SISA BUDGET (Rumus Diperbaiki: Limit Profil + Total Pemasukan - Total Pengeluaran)
    val remainingBudget: StateFlow<Double> = combine(
        dailyLimit,
        totalIncome,   // Kita ajak totalIncome masuk ke perhitungan
        totalExpense
    ) { limit, income, expense ->
        // Logikanya: Modal awal ditambah uang tambahan, dikurangi belanja
        val sisa = (limit + income) - expense
        if (sisa < 0) 0.0 else sisa
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)
    // 7. Indomie Index
    val indomieIndex: StateFlow<Int> = combine(
        remainingBudget,
        repository.currentIndomiePrice
    ) { sisaUang, priceString ->
        val price = priceString.toDoubleOrNull() ?: 3500.0
        if (price > 0 && sisaUang > 0) (sisaUang / price).toInt() else 0
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
}