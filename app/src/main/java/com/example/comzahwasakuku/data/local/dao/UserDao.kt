package com.example.comzahwasakuku.data.local.dao

import androidx.room.*
import com.example.comzahwasakuku.data.local.entity.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: UserEntity)


    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun loginLocal(email: String, password: String): UserEntity?
    @Query("UPDATE users SET target_limit = :limit, indomie_price = :price WHERE email = :email")
    suspend fun updateUserProfile(email: String, limit: Double, price: Double)

    @Query("UPDATE users SET last_login_date = :date WHERE email = :email")
    suspend fun updateLastLogin(email: String, date: Long)
}