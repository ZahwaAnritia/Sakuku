package com.example.comzahwasakuku.data.model

data class User(
    val nama: String,
    val email: String,
    val token: String,

    val targetLimit: Double = 0.0,
    val indomiePrice: Double = 3500.0
)