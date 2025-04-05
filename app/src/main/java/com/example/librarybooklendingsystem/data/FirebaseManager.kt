package com.example.librarybooklendingsystem.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object FirebaseManager {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth

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

    // Tính ngày trả dự kiến (100 ngày từ ngày mượn)
    fun calculateExpectedReturnDate(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_MONTH, 100) // Thêm 100 ngày
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(calendar.time)
    }

    // Tạo short ID cho người dùng
    private fun generateShortId(): String {
        val allowedChars = ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { allowedChars.random() }
            .joinToString("")
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

    // Thêm phương thức mới để lấy thông tin người dùng
    suspend fun getUserInfo(userId: String): Map<String, Any>? {
        return try {
            val document = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            
            if (document.exists()) {
                document.data
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy thông tin người dùng: ${e.message}")
            null
        }
    }

    // Thêm phương thức getUserData để tương thích với BorrowBookScreen
    suspend fun getUserData(userId: String): Map<String, Any>? {
        return getUserInfo(userId)
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
            Log.d("FirebaseManager", "Tạo yêu cầu mượn sách với dữ liệu: $borrowData")
            Log.d("FirebaseManager", "Tên tác giả trong yêu cầu: ${borrowData["author_name"]}")

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
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Đang mượn")
                .get()
                .await()

            val books = mutableListOf<Map<String, Any>>()
            for (document in querySnapshot.documents) {
                val bookData = document.data
                if (bookData != null) {
                    Log.d("FirebaseManager", "Dữ liệu sách mượn: $bookData")
                    books.add(bookData)
                }
            }
            books
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy sách đã mượn: ${e.message}")
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

    // Đăng ký user với short ID
    suspend fun registerUserWithShortId(email: String, password: String, name: String): String? {
        return try {
            // Tạo user với email và password
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Tạo short ID
                val shortId = generateShortId()
                
                // Lưu thông tin user vào Firestore
                val userData = mapOf(
                    "email" to email,
                    "role" to "user",
                    "shortId" to shortId,
                    "name" to name,  // Thêm tên người dùng
                    "createdAt" to Date()
                )

                db.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .set(userData)
                    .await()

                shortId
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi đăng ký user: ${e.message}")
            throw e
        }
    }

    suspend fun returnBook(userId: String, book: Map<String, Any>) {
        try {
            Log.d("FirebaseManager", "Bắt đầu trả sách cho user: $userId")
            val bookId = book["bookId"] as? String ?: return
            val currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
            
            // Tìm document chứa thông tin mượn sách
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("status", "Đang mượn")  // Chỉ cập nhật sách đang mượn
                .get()
                .await()

            Log.d("FirebaseManager", "Số lượng document tìm thấy: ${querySnapshot.size()}")
            
            if (querySnapshot.documents.isEmpty()) {
                Log.e("FirebaseManager", "Không tìm thấy thông tin mượn sách")
                return
            }

            // Cập nhật trạng thái sách thành "Đã trả"
            for (document in querySnapshot.documents) {
                try {
                    val updateData = mapOf(
                        "status" to "Đã trả",
                        "returnDate" to currentDate
                    )
                    
                    Log.d("FirebaseManager", "Cập nhật document ${document.id} với dữ liệu: $updateData")
                    
                    document.reference.update(updateData).await()
                    
                    Log.d("FirebaseManager", "Đã cập nhật trạng thái sách thành công")
                } catch (e: Exception) {
                    Log.e("FirebaseManager", "Lỗi khi cập nhật document ${document.id}: ${e.message}")
                    throw e
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi trả sách: ${e.message}")
            Log.e("FirebaseManager", "Stack trace: ${e.stackTraceToString()}")
            throw e
        }
    }

    // Kiểm tra xem người dùng đã từng mượn sách này chưa và chưa trả
    suspend fun hasUserBorrowedBook(userId: String, bookId: String): Boolean {
        return try {
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("status", "Đang mượn")
                .get()
                .await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi kiểm tra lịch sử mượn sách: ${e.message}")
            false
        }
    }

    suspend fun borrowBook(userId: String, book: Map<String, Any>) {
        try {
            Log.d("FirebaseManager", "Bắt đầu mượn sách cho user: $userId")
            Log.d("FirebaseManager", "Dữ liệu sách gốc: $book")
            
            // Kiểm tra xem người dùng đã mượn sách này chưa
            val bookId = book["id"] as? String ?: ""
            if (hasUserBorrowedBook(userId, bookId)) {
                throw Exception("Bạn đã mượn sách này và chưa trả!")
            }
            
            val borrowData = mapOf(
                "userId" to userId,
                "bookId" to bookId,
                "bookTitle" to (book["title"] as? String ?: ""),
                "author_name" to (book["author"] as? String ?: ""),
                "bookCover" to (book["coverUrl"] as? String ?: ""),
                "studentName" to (book["studentName"] as? String ?: ""),
                "borrowDate" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                "expectedReturnDate" to (book["expectedReturnDate"] as? String ?: ""),
                "status" to "Đang mượn"
            )

            Log.d("FirebaseManager", "Dữ liệu mượn sách: $borrowData")
            
            db.collection(BORROWS_COLLECTION)
                .add(borrowData)
                .await()

            Log.d("FirebaseManager", "Đã thêm thông tin mượn sách thành công")
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi mượn sách: ${e.message}")
            throw e
        }
    }

    suspend fun getBorrowHistory(userId: String): List<Map<String, Any>>? {
        return try {
            Log.d("FirebaseManager", "Đang lấy lịch sử mượn sách cho user: $userId")
            
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("status", "Đã trả")
                .get()
                .await()

            Log.d("FirebaseManager", "Số lượng sách đã trả: ${querySnapshot.size()}")
            
            val books = mutableListOf<Map<String, Any>>()
            for (document in querySnapshot.documents) {
                val bookData = document.data
                if (bookData != null) {
                    Log.d("FirebaseManager", "Dữ liệu sách: $bookData")
                    books.add(bookData)
                }
            }
            books
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy lịch sử mượn sách: ${e.message}")
            null
        }
    }
} 