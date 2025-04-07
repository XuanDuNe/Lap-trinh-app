package com.example.librarybooklendingsystem.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.librarybooklendingsystem.data.Book
import com.example.librarybooklendingsystem.data.FirebaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class BooksUiState {
    object Loading : BooksUiState()
    data class Success(val books: List<Book>) : BooksUiState()
    data class Error(val message: String) : BooksUiState()
}

class BookViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<BooksUiState>(BooksUiState.Loading)
    val uiState: StateFlow<BooksUiState> = _uiState.asStateFlow()

    init {
        Log.d("BookViewModel", "ViewModel được khởi tạo")
        loadBooks()
    }

    fun loadBooks() {
        Log.d("BookViewModel", "Bắt đầu loadBooks()")
        viewModelScope.launch {
            try {
                _uiState.value = BooksUiState.Loading
                Log.d("BookViewModel", "Đang gọi FirebaseManager.getAllBooks()")
                
                val booksList = FirebaseManager.getAllBooks()
                Log.d("BookViewModel", "Đã nhận được ${booksList.size} sách từ Firebase")
                
                if (booksList.isEmpty()) {
                    Log.d("BookViewModel", "Không có sách, thử thêm sách mẫu")
                    FirebaseManager.addSampleBook()
                    val updatedList = FirebaseManager.getAllBooks()
                    _uiState.value = BooksUiState.Success(updatedList)
                } else {
                    _uiState.value = BooksUiState.Success(booksList)
                }
                
            } catch (e: Exception) {
                Log.e("BookViewModel", "Lỗi khi load sách: ${e.message}")
                _uiState.value = BooksUiState.Error(e.message ?: "Đã xảy ra lỗi khi tải sách")
            }
        }
    }

    fun getBooksByCategory(category: String) {
        viewModelScope.launch {
            try {
                _uiState.value = BooksUiState.Loading
                Log.d("BookViewModel", "Đang tải sách theo thể loại: $category")
                val books = FirebaseManager.getBooksByCategory(category)
                Log.d("BookViewModel", "Đã tải được ${books.size} sách theo thể loại $category")
                _uiState.value = BooksUiState.Success(books)
            } catch (e: Exception) {
                Log.e("BookViewModel", "Lỗi khi tải sách theo thể loại: ${e.message}")
                _uiState.value = BooksUiState.Error(e.message ?: "Đã xảy ra lỗi khi tải sách theo thể loại")
            }
        }
    }
} 