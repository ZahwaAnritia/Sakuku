package com.example.comzahwasakuku.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.comzahwasakuku.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val _userFlow = MutableStateFlow<User?>(null)
    val userFlow: StateFlow<User?> = _userFlow.asStateFlow()

    companion object {
        const val KEY_TOKEN = "token"
        const val KEY_NAME = "name"
        const val KEY_EMAIL = "email"

        const val KEY_LIMIT = "limit"
        const val KEY_PRICE = "price"
    }

    init {
        val token = prefs.getString(KEY_TOKEN, null)
        val name = prefs.getString(KEY_NAME, null)
        val email = prefs.getString(KEY_EMAIL, "")
        // Load Limit & Price (Default 0.0 & 3500.0 jika belum ada)
        val limit = prefs.getFloat(KEY_LIMIT, 0f).toDouble()
        val price = prefs.getFloat(KEY_PRICE, 3500f).toDouble()

        if (token != null && name != null) {
            _userFlow.value = User(name, email ?: "", token, limit, price)
        }
    }

    fun saveUser(user: User) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, user.token)
        editor.putString(KEY_NAME, user.nama)
        editor.putString(KEY_EMAIL, user.email)
        // Simpan Limit & Price
        editor.putFloat(KEY_LIMIT, user.targetLimit.toFloat())
        editor.putFloat(KEY_PRICE, user.indomiePrice.toFloat())
        editor.apply()

        _userFlow.value = user
    }

    fun getUserName(): String? = prefs.getString(KEY_NAME, "Mahasiswa")

    fun clearSession() {
        prefs.edit().clear().apply()
        _userFlow.value = null
    }
}