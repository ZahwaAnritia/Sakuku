package com.example.comzahwasakuku.data.model

import com.google.gson.annotations.SerializedName

// 1. AUTH
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val nama: String, val email: String, val password: String, val target_limit: Double)
data class LoginResponse(val error: Boolean, val message: String, val data: User?)
typealias AuthResponse = LoginResponse

// 2. USER UPDATE
data class UpdateProfileRequest(val target_limit: Double, val indomie_price: Double)

// 3. TRANSACTION
data class TransactionRequest(
    val amount: Double,
    val kategori_id: Int,
    val note: String,
    val type: String,
    @SerializedName("trans_date")
    val date: String
)

data class TransactionListResponse(
    val error: Boolean,
    val message: String? = null,
    val count: Int?,
    val data: List<TransactionItem>?
)

data class TransactionItem(
    val trans_id: Int,
    val amount: Double,
    val note: String,
    val trans_date: String,
    val type: String,
    val kategori_name: String,
    val icon: String?
)

// 4. KATEGORI
data class AddCategoryRequest(val name: String, val type: String = "OUT", val icon: String = "ic_others")
data class CategoryItem(val kategori_id: Int, val user_id: Int?, val name: String, val type: String, val icon: String?, val is_default: Int = 0)
data class CategoryResponse(val error: Boolean, val message: String? = null, val data: List<CategoryItem>)
data class CategoryActionResponse(val error: Boolean, val message: String)

// 5. DASHBOARD (SUDAH DIPERBAIKI)
data class DashboardResponse(val error: Boolean, val message: String, val data: DashboardData?)
data class DashboardData(
    val sisa_saldo: Double,
    val total_pemasukan: Double,
    val total_pengeluaran: Double,
    @SerializedName("target_limit") val limit_harian: Double?,
    val indomie_index: Int,
    val indomie_price: Double, // <--- SUDAH DITAMBAHKAN
    val recent_transactions: List<TransactionItem>
)

// 6. GENERAL
data class BaseResponse(val error: Boolean, val message: String)