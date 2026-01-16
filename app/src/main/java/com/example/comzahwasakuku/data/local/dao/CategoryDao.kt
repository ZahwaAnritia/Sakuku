package com.example.comzahwasakuku.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.comzahwasakuku.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE user_id = :userId OR user_id = 0 ORDER BY name ASC")
    fun getAllCategories(userId: Int): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)
    // -------------------------------------------------

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCount(): Int

    @Query("UPDATE categories SET name = :newName WHERE name = :oldName AND user_id = 0")
    suspend fun renameSystemCategory(oldName: String, newName: String)
}