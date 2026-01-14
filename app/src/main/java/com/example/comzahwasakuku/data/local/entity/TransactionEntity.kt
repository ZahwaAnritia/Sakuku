package com.example.comzahwasakuku.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "trans_id")
    val id: Int = 0,

    @ColumnInfo(name = "user_id") // FOREIGN KEY KE USER (INT)
    val userId: Int,

    val amount: Double,
    val category: String, // Tetap simpan nama kategori (Denormalisasi)
    val type: String,     // "IN" atau "OUT"
    val note: String,
    val date: Long
)