package com.example.librarybooklendingsystem.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Category(
    val id: String,
    val name: String
)

class CategoryViewModel : ViewModel() {
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _categories.value = listOf(
                Category("Tiểu thuyết", "Tiểu thuyết"),
                Category("Văn học", "Văn học"),
                Category("Văn học thiếu nhi", "Văn học thiếu nhi"),
                Category("Khoa học viễn tưởng", "Khoa học viễn tưởng"),
                Category("Kỹ năng sống", "Kỹ năng sống"),
                Category("Văn Học", "Văn Học")
            )
        }
    }

    fun setSelectedCategory(categoryId: String) {
        _selectedCategory.value = categoryId
    }

    fun clearSelectedCategory() {
        _selectedCategory.value = null
    }
} 