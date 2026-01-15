package com.example.comzahwasakuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comzahwasakuku.data.local.entity.UserEntity
import com.example.comzahwasakuku.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // --- DATA FLOW ---
    // AMBIL USER ID DARI REPO
    val userId: StateFlow<Int> = repository.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), -1)

    val userName: StateFlow<String> = repository.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "User")

    val userEmail: StateFlow<String> = repository.userEmail
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "user@email.com")

    val currentLimit: StateFlow<String> = repository.currentLimit
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0")

    val currentIndomiePrice: StateFlow<String> = repository.currentIndomiePrice
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "3500")

    // --- FUNGSI LOGIN ---
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val isSuccess = repository.login(email, pass)
                if (isSuccess) {
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Email atau Password salah!")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }

    // --- FUNGSI REGISTER ---
    fun register(nama: String, email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val newUser = UserEntity(
                    email = email,
                    nama = nama,
                    sandi = pass,
                    targetLimit = 0.0,
                    indomiePrice = 3500.0
                )
                repository.register(newUser)

                // Setelah register sukses, user login manual
                _loginState.value = LoginState.Success

            } catch (e: android.database.sqlite.SQLiteConstraintException) {
                _loginState.value = LoginState.Error("Email sudah terdaftar!")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Gagal daftar: ${e.message}")
            }
        }
    }

    // --- UPDATE PROFILE ---
    fun updateProfile(limit: String, price: String) {
        viewModelScope.launch {
            val cleanLimit = limit.filter { it.isDigit() }
            val cleanPrice = price.filter { it.isDigit() }
            val email = userEmail.value
            repository.saveProfileSettings(cleanLimit, cleanPrice, email)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _loginState.value = LoginState.Idle
        }
    }

    fun resetState() {
        _loginState.value = LoginState.Idle
    }
}