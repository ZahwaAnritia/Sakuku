package com.example.comzahwasakuku.data.local.dao

import androidx.room.*
import com.example.comzahwasakuku.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: UserEntity)

    // Ubah sandi -> password sesuai Kamus Data SRS
    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginLocal(email: String, password: String): UserEntity?
    @Query("UPDATE users SET target_limit = :limit, indomie_price = :price WHERE email = :email")
    suspend fun updateUserProfile(email: String, limit: Double, price: Double)
}