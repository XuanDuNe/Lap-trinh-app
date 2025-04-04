package com.example.librarybooklendingsystem.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.util.*

object FirebaseManager {
    private val db: FirebaseFirestore = Firebase.firestore

    // Collection names
    private const val USERS_COLLECTION = "users"
    private const val ADMIN_COLLECTION = "admin"
    private const val BOOKS_COLLECTION = "books"
    private const val BORROWS_COLLECTION = "borrows"

    // Book status
    object BookStatus {
        const val AVAILABLE = "Có sẵn"
        const val BORROWED = "Đã mượn"
        const val PENDING = "Đang chờ duyệt"
    }

    // Borrow status
    object BorrowStatus {
        const val PENDING = "pending"
        const val APPROVED = "approved"
        const val RETURNED = "returned"
    }

    // User operations
    suspend fun createUser(userId: String, email: String, role: String = "user"): Boolean {
        return try {
            val userData = hashMapOf(
                "email" to email,
                "role" to role,
                "createdAt" to Date(),
                "isActive" to true
            )
            db.collection(USERS_COLLECTION)
                .document(userId)
                .set(userData)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error creating user: ${e.message}")
            false
        }
    }

    suspend fun getUserRole(userId: String): String? {
        return try {
            val document = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            document.getString("role")
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error getting user role: ${e.message}")
            null
        }
    }

    // Book operations
    suspend fun getAllBooks(): List<Book> {
        return try {
            Log.d("FirebaseManager", "Bắt đầu lấy sách từ collection: $BOOKS_COLLECTION")

            // Thực hiện truy vấn
            val snapshot = db.collection(BOOKS_COLLECTION)
                .get()
                .await()

            Log.d("FirebaseManager", "Số lượng documents nhận được: ${snapshot.size()}")
            
            if (snapshot.isEmpty) {
                Log.d("FirebaseManager", "Collection books trống")
                return emptyList()
            }

            // Parse documents thành Book objects
            val books = snapshot.documents.mapNotNull { doc ->
                try {
                    Log.d("FirebaseManager", "Đang parse document ID: ${doc.id}")
                    Log.d("FirebaseManager", "Document data: ${doc.data}")
                    
                    val book = Book.fromMap(doc.id, doc.data ?: emptyMap())
                    Log.d("FirebaseManager", "Đã parse thành công sách: ${book.title}")
                    book
                } catch (e: Exception) {
                    Log.e("FirebaseManager", "Lỗi khi parse document ${doc.id}: ${e.message}")
                    Log.e("FirebaseManager", "Stack trace: ${e.stackTraceToString()}")
                    null
                }
            }

            Log.d("FirebaseManager", "Tổng số sách đã parse thành công: ${books.size}")
            Log.d("FirebaseManager", "Danh sách sách: ${books.map { it.title }}")
            
            books
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy sách từ Firebase: ${e.message}")
            Log.e("FirebaseManager", "Stack trace: ${e.stackTraceToString()}")
            throw e // Throw exception để ViewModel có thể xử lý
        }
    }

    suspend fun getBookById(bookId: String): Book? {
        return try {
            Log.d("FirebaseManager", "Tìm sách với ID: $bookId")
            val doc = db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .get()
                .await()
            if (doc.exists()) {
                val book = Book.fromMap(doc.id, doc.data ?: emptyMap())
                Log.d("FirebaseManager", "Đã tìm thấy sách: ${book.title}")
                book
            } else {
                Log.d("FirebaseManager", "Không tìm thấy sách với ID: $bookId")
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi tìm sách theo ID: ${e.message}")
            null
        }
    }

    suspend fun getBooksByCategory(category: String): List<Book> {
        return try {
            Log.d("FirebaseManager", "Tìm sách theo thể loại: $category")
            val snapshot = db.collection(BOOKS_COLLECTION)
                .whereEqualTo("category", category)
                .get()
                .await()
            val books = snapshot.documents.mapNotNull { doc ->
                Book.fromMap(doc.id, doc.data ?: emptyMap())
            }
            Log.d("FirebaseManager", "Đã tìm thấy ${books.size} sách thuộc thể loại $category")
            books
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi tìm sách theo thể loại: ${e.message}")
            emptyList()
        }
    }

    suspend fun addBook(book: Book): String? {
        return try {
            val docRef = db.collection(BOOKS_COLLECTION)
                .add(Book.toMap(book))
                .await()
            docRef.id
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error adding book: ${e.message}")
            null
        }
    }

    suspend fun updateBook(bookId: String, book: Book): Boolean {
        return try {
            db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .set(Book.toMap(book), SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error updating book: ${e.message}")
            false
        }
    }

    suspend fun deleteBook(bookId: String): Boolean {
        return try {
            db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error deleting book: ${e.message}")
            false
        }
    }

    suspend fun incrementBorrowCount(bookId: String): Boolean {
        return try {
            db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .update("borrowCount", com.google.firebase.firestore.FieldValue.increment(1))
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error incrementing borrow count: ${e.message}")
            false
        }
    }

    suspend fun updateBookStatus(bookId: String, status: String): Boolean {
        return try {
            val updateData = hashMapOf(
                "status" to status
            )
            db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .set(updateData, SetOptions.merge())
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error updating book status: ${e.message}")
            false
        }
    }

    // Thêm một cuốn sách mẫu để test
    suspend fun addSampleBook() {
        try {
            val sampleBook = mapOf(
                "title" to "Sách mẫu",
                "author" to "Tác giả mẫu",
                "category" to "Văn học",
                "status" to BookStatus.AVAILABLE,
                "coverUrl" to "https://picsum.photos/200/300",
                "description" to "Đây là sách mẫu để test",
                "createdAt" to Date(),
                "borrowCount" to 0
            )

            db.collection(BOOKS_COLLECTION)
                .add(sampleBook)
                .await()

            Log.d("FirebaseManager", "Đã thêm sách mẫu thành công")
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi thêm sách mẫu: ${e.message}")
            throw e // Throw exception để ViewModel có thể xử lý
        }
    }

    // Borrow operations
    suspend fun createBorrowRequest(borrowData: Map<String, Any>): String? {
        return try {
            Log.d("FirebaseManager", "Tạo yêu cầu mượn sách: $borrowData")
            
            // Kiểm tra user đã đăng nhập
            val userId = borrowData["userId"] as? String
            if (userId.isNullOrEmpty()) {
                throw Exception("Bạn cần đăng nhập để mượn sách")
            }

            // Kiểm tra sách tồn tại
            val bookId = borrowData["bookId"] as? String
            if (bookId.isNullOrEmpty()) {
                throw Exception("Không tìm thấy thông tin sách")
            }

            // Thêm yêu cầu mượn sách
            val docRef = db.collection(BORROWS_COLLECTION)
                .add(borrowData)
                .await()

            // Cập nhật trạng thái sách
            db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .update("status", BookStatus.PENDING)
                .await()

            Log.d("FirebaseManager", "Đã tạo yêu cầu mượn sách thành công với ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi tạo yêu cầu mượn sách: ${e.message}")
            throw e
        }
    }

    // Lấy danh sách sách đã mượn của user
    suspend fun getUserBorrowedBooks(userId: String): List<Map<String, Any>>? {
        return try {
            Log.d("FirebaseManager", "Đang lấy danh sách sách đã mượn cho user: $userId")
            val snapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            if (snapshot.isEmpty) {
                Log.d("FirebaseManager", "Không có sách nào được mượn")
                return emptyList()
            }

            val borrowedBooks = snapshot.documents.map { doc ->
                val data = doc.data ?: emptyMap()
                mapOf(
                    "borrowId" to doc.id,
                    "bookId" to (data["bookId"] as? String ?: ""),
                    "bookTitle" to (data["bookTitle"] as? String ?: ""),
                    "bookCover" to (data["bookCover"] as? String ?: ""),
                    "studentName" to (data["studentName"] as? String ?: ""),
                    "expectedReturnDate" to (data["expectedReturnDate"] as? String ?: ""),
                    "status" to (data["status"] as? String ?: ""),
                    "borrowDate" to (data["borrowDate"] as? Long ?: 0L)
                )
            }

            Log.d("FirebaseManager", "Đã tìm thấy ${borrowedBooks.size} sách đã mượn")
            borrowedBooks
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy danh sách sách đã mượn: ${e.message}")
            Log.e("FirebaseManager", "Stack trace: ${e.stackTraceToString()}")
            null
        }
    }

    // Admin operations
    suspend fun createAdmin(userId: String, email: String): Boolean {
        return try {
            val adminData = hashMapOf(
                "email" to email,
                "createdAt" to Date(),
                "isActive" to true
            )
            db.collection(ADMIN_COLLECTION)
                .document(userId)
                .set(adminData)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Error creating admin: ${e.message}")
            false
        }
    }
} 