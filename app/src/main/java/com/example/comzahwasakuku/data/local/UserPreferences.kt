package com.example.comzahwasakuku.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Membuat DataStore tunggal untuk aplikasi
private val Context.dataStore by preferencesDataStore(name = "user_session")

class UserPreferences(private val context: Context) {

    companion object {
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ID = intPreferencesKey("user_id") // <--- KUNCI UTAMA BARU KITA
        val USER_NAME = stringPreferencesKey("user_name")
    }

    // Ambil User ID (Default -1 jika belum login)
    val userId: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[USER_ID] ?: -1
    }

    // Ambil Email (Opsional, buat display di profile)
    val userSession: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_EMAIL]
    }

    // Ambil Nama
    val userName: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[USER_NAME]
    }

    // Simpan Data Login (Termasuk ID)
    suspend fun saveUserSession(email: String, id: Int, name: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
            preferences[USER_ID] = id // PENTING: Simpan ID Integer
            preferences[USER_NAME] = name
        }
    }

    // Logout (Hapus semua)
    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}