package com.example.comzahwasakuku.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.comzahwasakuku.data.local.dao.UserDao
import com.example.comzahwasakuku.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Setup DataStore
private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class AuthRepository(
    private val context: Context,
    private val userDao: UserDao
) {

    companion object {
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_ID = intPreferencesKey("user_id") // <--- TAMBAHAN PENTING
        val KEY_USER_LIMIT = stringPreferencesKey("user_limit")
        val KEY_INDOMIE_PRICE = stringPreferencesKey("indomie_price")
    }

    // --- MEMBACA DATA (GET) ---
    val userId: Flow<Int> = context.dataStore.data.map { pref -> pref[KEY_USER_ID] ?: -1 } // Default -1
    val userSession: Flow<String?> = context.dataStore.data.map { pref -> pref[KEY_USER_NAME] }
    val userName: Flow<String> = context.dataStore.data.map { pref -> pref[KEY_USER_NAME] ?: "User" }
    val userEmail: Flow<String> = context.dataStore.data.map { pref -> pref[KEY_USER_EMAIL] ?: "user@email.com" }
    val currentLimit: Flow<String> = context.dataStore.data.map { pref -> pref[KEY_USER_LIMIT] ?: "0" }
    val currentIndomiePrice: Flow<String> = context.dataStore.data.map { pref -> pref[KEY_INDOMIE_PRICE] ?: "3500" }

    // 1. REGISTER
    suspend fun register(user: UserEntity) {
        userDao.registerUser(user)
    }

    // 2. LOGIN: Ambil User dari DB, Simpan ID-nya
    suspend fun login(email: String, sandi: String): Boolean {
        val userFromDb = userDao.loginLocal(email, sandi)

        if (userFromDb != null) {
            context.dataStore.edit { pref ->
                pref[KEY_USER_ID] = userFromDb.id // <--- SIMPAN ID INT
                pref[KEY_USER_NAME] = userFromDb.nama
                pref[KEY_USER_EMAIL] = userFromDb.email
                pref[KEY_USER_LIMIT] = userFromDb.targetLimit.toLong().toString()
                pref[KEY_INDOMIE_PRICE] = userFromDb.indomiePrice.toLong().toString()
            }
            return true
        } else {
            return false
        }
    }

    // 3. UPDATE PROFIL
    suspend fun saveProfileSettings(limit: String, price: String, email: String) {
        context.dataStore.edit { pref ->
            pref[KEY_USER_LIMIT] = limit
            pref[KEY_INDOMIE_PRICE] = price
        }
        val limitDouble = limit.toDoubleOrNull() ?: 0.0
        val priceDouble = price.toDoubleOrNull() ?: 3500.0
        userDao.updateUserProfile(email, limitDouble, priceDouble)
    }

    // 4. MANUAL SAVE SESSION (Opsional saat Register)
    suspend fun saveUserSession(name: String, email: String) {
        context.dataStore.edit { pref ->
            pref[KEY_USER_NAME] = name
            pref[KEY_USER_EMAIL] = email
            // Note: ID tidak bisa disimpan di sini karena register belum tentu return ID
            // User disarankan Login ulang setelah register agar ID tersimpan via fungsi login()
        }
    }

    // 5. LOGOUT
    suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}