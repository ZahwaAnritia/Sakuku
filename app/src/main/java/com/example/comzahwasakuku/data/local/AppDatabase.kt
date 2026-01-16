package com.example.comzahwasakuku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.comzahwasakuku.data.local.dao.CategoryDao
import com.example.comzahwasakuku.data.local.dao.TransactionDao
import com.example.comzahwasakuku.data.local.dao.UserDao
import com.example.comzahwasakuku.data.local.entity.CategoryEntity
import com.example.comzahwasakuku.data.local.entity.TransactionEntity
import com.example.comzahwasakuku.data.local.entity.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, UserEntity::class],
    version = 10,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
    abstract fun userDao(): UserDao

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val categoryDao = database.categoryDao()


                    categoryDao.renameSystemCategory("Uang Saku", "Lainnya")


                    populateDatabaseIfEmpty(categoryDao)
                }
            }
        }

        suspend fun populateDatabaseIfEmpty(categoryDao: CategoryDao) {
            if (categoryDao.getCount() == 0) {

                val systemId = 0


                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Makan", type = "OUT", icon = "Makan"))
                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Transport", type = "OUT", icon = "Transport"))
                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Belanja", type = "OUT", icon = "Belanja"))
                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Hiburan", type = "OUT", icon = "Hiburan"))
                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Kesehatan", type = "OUT", icon = "Kesehatan"))
                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Pendidikan", type = "OUT", icon = "School"))


                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Gaji", type = "IN", icon = "Uang"))
                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Bonus", type = "IN", icon = "Uang"))
                categoryDao.insertCategory(CategoryEntity(userId = systemId, name = "Lainnya", type = "IN", icon = "Uang"))
            }

        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zahwasakuku_db"
                )
                    .fallbackToDestructiveMigration() // Hapus data lama biar ga crash
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}