package com.example.comzahwasakuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comzahwasakuku.data.local.entity.TransactionEntity
import com.example.comzahwasakuku.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Fungsi Add terima userId Int
    fun addTransaction(
        userId: Int,
        amount: Double,
        category: String,
        note: String,
        type: String,
        date: Long,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        if (amount <= 0.0) {
            onError("Nominal transaksi tidak boleh 0 atau kosong!")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val transaction = TransactionEntity(
                    userId = userId,
                    amount = amount,
                    category = category,
                    note = note,
                    type = type,
                    date = date
                )
                repository.insertTransaction(transaction)
                _isLoading.value = false
                onSuccess()
            } catch (e: Exception) {
                _isLoading.value = false
                onError(e.message ?: "Gagal menyimpan transaksi")
            }
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}