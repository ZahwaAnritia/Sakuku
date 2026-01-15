package com.example.comzahwasakuku.data.model

import com.google.gson.annotations.SerializedName

// 1. AUTH
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val nama: String, val email: String, val password: String, val target_limit: Double)
data class LoginResponse(val error: Boolean, val message: String, val data: User?)
typealias AuthResponse = LoginResponse

// 2. USER UPDATE
data class UpdateProfileRequest(val target_limit: Double, val indomie_price: Double)
