package com.example.comzahwasakuku.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "kategori_id")
    val id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    val name: String,
    val type: String,
    val icon: String
)