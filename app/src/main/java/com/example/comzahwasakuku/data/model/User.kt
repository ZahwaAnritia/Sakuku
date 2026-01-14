package com.example.comzahwasakuku.data.model

data class User(
    val nama: String,
    val email: String,
    val token: String,
    // Tambahkan 2 baris ini agar updateProfile tidak error
    val targetLimit: Double = 0.0,
    val indomiePrice: Double = 3500.0 // Default harga indomie
)