package com.example.comzahwasakuku.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    // BARIS INI YANG MEMBUAT EMAIL TIDAK BISA KEMBAR
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    // --- REVISI: TAMBAHKAN COLUMN INFO ---
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id") // <--- Database baca ini sebagai 'user_id'
    val id: Int = 0,              // <--- Kotlin tetap baca ini sebagai 'id'
    // -------------------------------------

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "name")
    val nama: String,

    @ColumnInfo(name = "password")
    val sandi: String,

    @ColumnInfo(name = "target_limit")
    val targetLimit: Double,

    @ColumnInfo(name = "indomie_price")
    val indomiePrice: Double = 3500.0,

    @ColumnInfo(name = "utang_carryover")
    val utangCarryover: Double = 0.0,

    @ColumnInfo(name = "last_login_date")
    val lastLoginDate: Long = 0L
)