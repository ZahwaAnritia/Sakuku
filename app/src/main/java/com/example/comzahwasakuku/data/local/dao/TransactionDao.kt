package com.example.comzahwasakuku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.comzahwasakuku.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    // Filter by User ID (Int)
    @Query("SELECT * FROM transactions WHERE user_id = :userId ORDER BY date DESC")
    fun getAllTransactions(userId: Int): Flow<List<TransactionEntity>>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'IN' AND user_id = :userId")
    fun getTotalIncome(userId: Int): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'OUT' AND user_id = :userId")
    fun getTotalExpense(userId: Int): Flow<Double?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)


    @Query("SELECT SUM(amount) FROM transactions WHERE type = 'OUT' AND user_id = :userId AND date BETWEEN :start AND :end")
    fun getExpenseByDate(userId: Int, start: Long, end: Long): Flow<Double?>

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)
}