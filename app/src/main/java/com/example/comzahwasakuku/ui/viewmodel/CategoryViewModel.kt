package com.example.comzahwasakuku.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comzahwasakuku.data.local.entity.CategoryEntity
import com.example.comzahwasakuku.data.repository.CategoryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CategoryViewModel(
    private val repository: CategoryRepository
) : ViewModel() {

    // 1. Simpan User ID
    private val _userId = MutableStateFlow(-1)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 2.  Ambil Kategori
    @OptIn(ExperimentalCoroutinesApi::class)
    val categories: StateFlow<List<CategoryEntity>> = _userId.flatMapLatest { id ->
        if (id != -1) {
            repository.getAllCategories(id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // 3. Set User ID d
    fun setUserId(id: Int) {
        _userId.value = id
    }

    // 4. Add Category
    fun addCategory(userId: Int, name: String, type: String, icon: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val newCategory = CategoryEntity(
                    userId = userId,
                    name = name,
                    type = type,
                    icon = icon
                )
                repository.insertCategory(newCategory)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            // ID 0 = System, jangan dihapus
            if (category.userId != 0) {
                repository.deleteCategory(category)
            }
        }
    }
}