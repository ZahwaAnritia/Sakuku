package com.example.comzahwasakuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comzahwasakuku.data.local.entity.TransactionEntity
import com.example.comzahwasakuku.data.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

class LaporanViewModel(private val repository: TransactionRepository) : ViewModel() {


    fun getTransactions(userId: Int): Flow<List<TransactionEntity>> {
        return repository.getAllTransactions(userId)
    }

    // Fungsi Delete
    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }

    // Fungsi Filter
    fun filterTransactions(list: List<TransactionEntity>, filterType: String): List<TransactionEntity> {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        return when (filterType) {
            "Mingguan" -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val sevenDaysAgo = calendar.timeInMillis
                list.filter { it.date in sevenDaysAgo..now }
            }
            "Bulanan" -> {
                val currentMonth = calendar.get(Calendar.MONTH)
                val currentYear = calendar.get(Calendar.YEAR)
                list.filter {
                    val itemCal = Calendar.getInstance().apply { timeInMillis = it.date }
                    itemCal.get(Calendar.MONTH) == currentMonth && itemCal.get(Calendar.YEAR) == currentYear
                }
            }
            else -> list
        }
    }
}