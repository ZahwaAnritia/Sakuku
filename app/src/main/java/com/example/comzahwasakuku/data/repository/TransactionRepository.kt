package com.example.comzahwasakuku.data.repository

import com.example.comzahwasakuku.data.local.dao.TransactionDao
import com.example.comzahwasakuku.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }


    fun getAllTransactions(userId: Int): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions(userId)
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }
}