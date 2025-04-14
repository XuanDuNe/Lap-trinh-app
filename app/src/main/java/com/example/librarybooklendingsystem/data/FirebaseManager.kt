package com.example.librarybooklendingsystem.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.Timestamp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

object FirebaseManager {
    private val db: FirebaseFirestore = Firebase.firestore
    private val auth: FirebaseAuth = Firebase.auth
    private val analytics: FirebaseAnalytics by lazy { Firebase.analytics }
    private val _isAdmin = MutableStateFlow(false)
    private val _currentUserShortId = MutableStateFlow<String?>(null)
    private val _currentUserRole = MutableStateFlow<String?>(null)
    private lateinit var prefs: SharedPreferences

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val isAdmin: StateFlow<Boolean>
        get() = _isAdmin.asStateFlow()

    val currentUserShortId: StateFlow<String?>
        get() = _currentUserShortId.asStateFlow()

    val currentUserRole: StateFlow<String?>
        get() = _currentUserRole.asStateFlow()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences("FirebasePrefs", Context.MODE_PRIVATE)
        checkAdminStatus()
    }

    private fun checkAdminStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userDoc = db.collection(USERS_COLLECTION)
                        .document(currentUser.uid)
                        .get()
                        .await()

                    if (userDoc.exists()) {
                        val role = userDoc.getString("role")
                        Log.d("FirebaseManager", "User role from Firestore: $role")
                        val isAdmin = role == "admin"
                        _isAdmin.value = isAdmin
                        
                        // Update user role and admin status in AuthState
                        _currentUserRole.value = role
                        _isAdmin.value = isAdmin
                        
                        // Save admin status in SharedPreferences
                        prefs.edit().putBoolean("is_admin", isAdmin).apply()
                    } else {
                        Log.e("FirebaseManager", "User document not found")
                        _isAdmin.value = false
                        _currentUserRole.value = null
                        prefs.edit().putBoolean("is_admin", false).apply()
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseManager", "Error checking admin status: ${e.message}")
                    _isAdmin.value = false
                    _currentUserRole.value = null
                    prefs.edit().putBoolean("is_admin", false).apply()
                }
            }
        } else {
            _isAdmin.value = false
            _currentUserRole.value = null
            prefs.edit().putBoolean("is_admin", false).apply()
        }
    }

    fun signOut() {
        auth.signOut()
        _isAdmin.value = false
        _currentUserShortId.value = null
        _currentUserRole.value = null
    }

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

    // Analytics event names
    private object AnalyticsEvents {
        const val BOOK_BORROW = "book_borrow"
        const val BOOK_RETURN = "book_return"
    }

    // Analytics parameter names
    private object AnalyticsParams {
        const val USER_ID = "user_id"
        const val BOOK_ID = "book_id"
        const val BOOK_TITLE = "book_title"
        const val BORROW_TIME = "borrow_time"
        const val RETURN_TIME = "return_time"
        const val EMAIL = "email"
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
            val doc = db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .get()
                .await()

            if (doc.exists()) {
                Book.fromMap(doc.id, doc.data ?: emptyMap())
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy thông tin sách: ${e.message}")
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

            // Kiểm tra xem người dùng đã mượn sách này và chưa trả hay chưa
            val existingBorrow = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("status", "Đang mượn")
                .get()
                .await()

            if (!existingBorrow.isEmpty) {
                throw Exception("Bạn đã mượn sách này và chưa trả. Vui lòng trả sách trước khi mượn lại.")
            }

            // Thêm yêu cầu mượn sách với trạng thái pending
            val borrowRequest = borrowData.toMutableMap().apply {
                put("status", "pending")
                put("createdAt", Timestamp(Date()))
            }

            val docRef = db.collection(BORROWS_COLLECTION)
                .add(borrowRequest)
                .await()

            Log.d("FirebaseManager", "Đã tạo yêu cầu mượn sách thành công với ID: ${docRef.id}")
            docRef.id
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi tạo yêu cầu mượn sách: ${e.message}")
            throw e
        }
    }

    // Thêm phương thức để cập nhật số lượng sách
    suspend fun updateBookQuantity(bookId: String, increment: Int): Boolean {
        return try {
            val bookDoc = db.collection(BOOKS_COLLECTION)
                .document(bookId)
                .get()
                .await()

            val bookData = bookDoc.data
            if (bookData != null) {
                val currentQuantity = (bookData["quantity"] as? Long)?.toInt() ?: 0
                val newQuantity = maxOf(0, currentQuantity + increment) // Đảm bảo số lượng không âm
                
                // Cập nhật số lượng và trạng thái sách
                val updateData = mapOf(
                    "quantity" to newQuantity,
                    "status" to if (newQuantity > 0) BookStatus.AVAILABLE else "Không có sẵn"
                ) as Map<String, Any>
                
                db.collection(BOOKS_COLLECTION)
                    .document(bookId)
                    .update(updateData)
                    .await()
                
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi cập nhật số lượng sách: ${e.message}")
            false
        }
    }

    suspend fun approveBorrowRequest(borrowId: String) {
        try {
            // Lấy thông tin yêu cầu mượn sách
            val borrowDoc = db.collection(BORROWS_COLLECTION)
                .document(borrowId)
                .get()
                .await()

            val borrowData = borrowDoc.data
            if (borrowData != null) {
                val bookId = borrowData["bookId"] as? String
                val expectedReturnDate = borrowData["expectedReturnDate"] as? String
                
                // Cập nhật trạng thái yêu cầu mượn sách
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val returnDate = try {
                    dateFormat.parse(expectedReturnDate)
                } catch (e: Exception) {
                    // Nếu không có ngày trả dự kiến hoặc định dạng không hợp lệ, sử dụng ngày mặc định (100 ngày sau)
                    val calendar = Calendar.getInstance()
                    calendar.add(Calendar.DAY_OF_MONTH, 100)
                    calendar.time
                }

                // Cập nhật số lượng sách (giảm 1)
                if (bookId != null) {
                    updateBookQuantity(bookId, -1)
                }

                db.collection(BORROWS_COLLECTION)
                    .document(borrowId)
                    .update(
                        mapOf(
                            "status" to "Đang mượn",
                            "approvedAt" to Timestamp(Date()),
                            "borrowDate" to Timestamp(Date()),
                            "expectedReturnDate" to Timestamp(returnDate)
                        )
                    )
                    .await()

                Log.d("FirebaseManager", "Đã duyệt yêu cầu mượn sách thành công")
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi duyệt yêu cầu mượn sách: ${e.message}")
            throw e
        }
    }

    suspend fun getPendingBorrowRequests(): List<Map<String, Any>> {
        return try {
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("status", "pending")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                doc.data?.toMutableMap()?.apply {
                    put("id", doc.id)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy danh sách yêu cầu mượn sách: ${e.message}")
            emptyList()
        }
    }

    // Lấy danh sách sách đã mượn của user
    suspend fun getUserBorrowedBooks(userId: String): List<Map<String, Any>> {
        return try {
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereIn("status", listOf("Đang mượn", "Chờ duyệt trả"))
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                doc.data?.toMutableMap()?.apply {
                    put("id", doc.id)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy danh sách sách đang mượn: ${e.message}")
            emptyList()
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

    // Analytics tracking functions
    private fun logBookBorrow(userId: String, bookId: String, bookTitle: String) {
        try {
            Log.d("FirebaseAnalytics", "Logging book borrow event - User: $userId, Book: $bookTitle")
            analytics.logEvent(AnalyticsEvents.BOOK_BORROW) {
                param(AnalyticsParams.USER_ID, userId)
                param(AnalyticsParams.BOOK_ID, bookId)
                param(AnalyticsParams.BOOK_TITLE, bookTitle)
                param(FirebaseAnalytics.Param.SUCCESS, "1")
                param(AnalyticsParams.BORROW_TIME, System.currentTimeMillis())
            }
            Log.d("FirebaseAnalytics", "Successfully logged book borrow event")
        } catch (e: Exception) {
            Log.e("FirebaseAnalytics", "Error logging book borrow event: ${e.message}")
        }
    }

    private fun logBookReturn(userId: String, bookId: String, bookTitle: String) {
        try {
            Log.d("FirebaseAnalytics", "Logging book return event - User: $userId, Book: $bookTitle")
            analytics.logEvent(AnalyticsEvents.BOOK_RETURN) {
                param(AnalyticsParams.USER_ID, userId)
                param(AnalyticsParams.BOOK_ID, bookId)
                param(AnalyticsParams.BOOK_TITLE, bookTitle)
                param(FirebaseAnalytics.Param.SUCCESS, "1")
                param(AnalyticsParams.RETURN_TIME, System.currentTimeMillis())
            }
            Log.d("FirebaseAnalytics", "Successfully logged book return event")
        } catch (e: Exception) {
            Log.e("FirebaseAnalytics", "Error logging book return event: ${e.message}")
        }
    }

    // Đăng ký user với short ID
    suspend fun registerUserWithShortId(email: String, password: String, name: String): String? {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                val shortId = generateShortId()
                
                val userData = mapOf(
                    "email" to email,
                    "role" to "user",
                    "shortId" to shortId,
                    "name" to name,
                    "createdAt" to Date()
                )

                db.collection(USERS_COLLECTION)
                    .document(user.uid)
                    .set(userData)
                    .await()

                // Track user registration
                analytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                    param(FirebaseAnalytics.Param.METHOD, "email")
                    param(AnalyticsParams.USER_ID, user.uid)
                    param(AnalyticsParams.EMAIL, email)
                    param(FirebaseAnalytics.Param.SUCCESS, "1")
                }

                shortId
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi đăng ký user: ${e.message}")
            throw e
        }
    }

    suspend fun loginUser(email: String, password: String): Boolean {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            
            if (user != null) {
                // Track login event
                analytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                    param(FirebaseAnalytics.Param.METHOD, "email")
                    param(AnalyticsParams.USER_ID, user.uid)
                    param(AnalyticsParams.EMAIL, email)
                    param(FirebaseAnalytics.Param.SUCCESS, "1")
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi đăng nhập: ${e.message}")
            false
        }
    }

    suspend fun returnBook(userId: String, book: Map<String, Any>) {
        try {
            Log.d("FirebaseManager", "Bắt đầu trả sách cho user: $userId")
            val bookId = book["bookId"] as? String ?: return
            val bookTitle = book["bookTitle"] as? String ?: ""
            
            // Tìm document chứa thông tin mượn sách
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("userId", userId)
                .whereEqualTo("bookId", bookId)
                .whereEqualTo("status", "Đang mượn")
                .get()
                .await()

            Log.d("FirebaseManager", "Số lượng document tìm thấy: ${querySnapshot.size()}")
            
            if (querySnapshot.documents.isEmpty()) {
                Log.e("FirebaseManager", "Không tìm thấy thông tin mượn sách")
                return
            }

            // Cập nhật trạng thái sách thành "Chờ duyệt trả"
            for (document in querySnapshot.documents) {
                try {
                    val updateData = mapOf(
                        "status" to "Chờ duyệt trả",
                        "returnRequestDate" to Timestamp(Date())
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
            val bookTitle = book["title"] as? String ?: ""
            
            if (hasUserBorrowedBook(userId, bookId)) {
                throw Exception("Bạn đã mượn sách này và chưa trả!")
            }
            
            val currentDate = Date() // Ngày mượn hiện tại
            
            // Sử dụng ngày trả dự kiến từ dữ liệu đầu vào
            val expectedReturnDateStr = book["expectedReturnDate"] as? String ?: ""
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val expectedReturnDate = try {
                dateFormat.parse(expectedReturnDateStr)
            } catch (e: Exception) {
                // Nếu không có ngày trả dự kiến hoặc định dạng không hợp lệ, sử dụng ngày mặc định (100 ngày sau)
                val calendar = Calendar.getInstance()
                calendar.time = currentDate
                calendar.add(Calendar.DAY_OF_MONTH, 100)
                calendar.time
            }
            
            val borrowData = mapOf(
                "userId" to userId,
                "bookId" to bookId,
                "bookTitle" to bookTitle,
                "author_name" to (book["author"] as? String ?: ""),
                "bookCover" to (book["coverUrl"] as? String ?: ""),
                "studentName" to (book["studentName"] as? String ?: ""),
                "borrowDate" to Timestamp(currentDate),
                "expectedReturnDate" to Timestamp(expectedReturnDate),
                "status" to "Đang mượn"
            )

            Log.d("FirebaseManager", "Dữ liệu mượn sách: $borrowData")
            
            // Lưu thông tin mượn sách
            db.collection(BORROWS_COLLECTION)
                .add(borrowData)
                .await()

            // Track book borrow event
            logBookBorrow(userId, bookId, bookTitle)

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

    fun signIn(
        email: String,
        password: String,
        context: Context,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val user = authResult.user
                if (user != null) {
                    // Get user role from Firestore
                    db.collection("users").document(user.uid).get()
                        .addOnSuccessListener { document ->
                            if (document != null && document.exists()) {
                                val role = document.getString("role")
                                _isAdmin.value = role == "admin"
                                _currentUserShortId.value = document.getString("shortId")
                                _currentUserRole.value = role
                                onSuccess()
                            } else {
                                onError("User data not found")
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("FirebaseManager", "Error getting user data", e)
                            onError("Error getting user data: ${e.message}")
                        }
                } else {
                    onError("Authentication failed")
                }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseManager", "Sign in failed", e)
                onError("Sign in failed: ${e.message}")
            }
    }

    fun createAdminAccount(
        email: String,
        password: String,
        shortId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // First check if shortId is unique
        db.collection("users")
            .whereEqualTo("shortId", shortId)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    onError("Short ID already exists")
                    return@addOnSuccessListener
                }

                // Create the user account
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { authResult ->
                        val user = authResult.user
                        if (user != null) {
                            // Create user document in Firestore
                            val userData = hashMapOf(
                                "email" to email,
                                "role" to "admin",
                                "shortId" to shortId
                            )

                            db.collection("users").document(user.uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    _isAdmin.value = true
                                    _currentUserShortId.value = shortId
                                    _currentUserRole.value = "admin"
                                    onSuccess()
                                }
                                .addOnFailureListener { e ->
                                    Log.e("FirebaseManager", "Error creating user document", e)
                                    onError("Error creating user document: ${e.message}")
                                }
                        } else {
                            onError("User creation failed")
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseManager", "Create user failed", e)
                        onError("Create user failed: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseManager", "Error checking shortId uniqueness", e)
                onError("Error checking shortId uniqueness: ${e.message}")
            }
    }

    // Thêm phương thức để lấy tất cả người dùng
    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = db.collection(USERS_COLLECTION)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    User.fromMap(doc.id, doc.data ?: emptyMap())
                } catch (e: Exception) {
                    Log.e("FirebaseManager", "Lỗi khi parse user document ${doc.id}: ${e.message}")
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy danh sách người dùng: ${e.message}")
            emptyList()
        }
    }

    // Thêm phương thức để cập nhật trạng thái người dùng
    suspend fun updateUserStatus(userId: String, isActive: Boolean): Boolean {
        return try {
            db.collection(USERS_COLLECTION)
                .document(userId)
                .update("isActive", isActive)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi cập nhật trạng thái người dùng: ${e.message}")
            false
        }
    }

    // Thêm phương thức để cập nhật thông tin người dùng
    suspend fun updateUserInfo(userId: String, email: String, role: String): Boolean {
        return try {
            val updateData = mapOf(
                "email" to email,
                "role" to role
            ) as Map<String, Any>
            
            db.collection(USERS_COLLECTION)
                .document(userId)
                .update(updateData)
                .await()
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi cập nhật thông tin người dùng: ${e.message}")
            false
        }
    }

    // Thêm phương thức để xóa tài khoản người dùng
    suspend fun deleteUser(userId: String): Boolean {
        return try {
            // Xóa document trong collection users
            db.collection(USERS_COLLECTION)
                .document(userId)
                .delete()
                .await()
            
            // Xóa tài khoản authentication
            auth.currentUser?.let { currentUser ->
                if (currentUser.uid == userId) {
                    currentUser.delete().await()
                }
            }
            
            true
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi xóa tài khoản người dùng: ${e.message}")
            false
        }
    }

    // Borrow operations
    suspend fun getAllBorrows(): List<Borrow> {
        return try {
            Log.d("FirebaseManager", "Bắt đầu lấy dữ liệu mượn sách từ collection: $BORROWS_COLLECTION")
            val snapshot = db.collection(BORROWS_COLLECTION).get().await()
            Log.d("FirebaseManager", "Số lượng documents mượn sách nhận được: ${snapshot.size()}")

            if (snapshot.isEmpty) {
                Log.d("FirebaseManager", "Collection borrows trống")
                return emptyList()
            }

            val borrows = snapshot.documents.mapNotNull { doc ->
                try {
                    Log.d("FirebaseManager", "Đang parse borrow document ID: ${doc.id}")
                    Log.d("FirebaseManager", "Borrow document data: ${doc.data}")
                    Borrow.fromMap(doc.id, doc.data ?: emptyMap())
                } catch (e: Exception) {
                    Log.e("FirebaseManager", "Lỗi khi parse borrow document ${doc.id}: ${e.message}", e)
                    null
                }
            }
            Log.d("FirebaseManager", "Tổng số lượt mượn đã parse thành công: ${borrows.size}")
            borrows
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy dữ liệu mượn sách từ Firebase: ${e.message}", e)
            emptyList()
        }
    }

    // Thêm hàm lấy tên người dùng
    suspend fun getUserName(userId: String): String {
        return try {
            val userDoc = db.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (userDoc.exists()) {
                userDoc.getString("name") ?: userDoc.getString("email") ?: "Không xác định"
            } else {
                "Không xác định"
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy tên người dùng: ${e.message}")
            "Không xác định"
        }
    }

    // Thêm hàm để tạo yêu cầu trả sách
    suspend fun createReturnRequest(borrowId: String): Boolean {
        return try {
            // Lấy thông tin mượn sách
            val borrowDoc = db.collection(BORROWS_COLLECTION)
                .document(borrowId)
                .get()
                .await()

            val borrowData = borrowDoc.data
            if (borrowData != null) {
                // Cập nhật trạng thái thành "Chờ duyệt trả"
                db.collection(BORROWS_COLLECTION)
                    .document(borrowId)
                    .update(
                        mapOf(
                            "status" to "Chờ duyệt trả",
                            "returnRequestDate" to Timestamp(Date())
                        )
                    )
                    .await()

                Log.d("FirebaseManager", "Đã tạo yêu cầu trả sách thành công")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi tạo yêu cầu trả sách: ${e.message}")
            false
        }
    }

    // Thêm hàm để lấy danh sách yêu cầu trả sách đang chờ duyệt
    suspend fun getPendingReturnRequests(): List<Map<String, Any>> {
        return try {
            val querySnapshot = db.collection(BORROWS_COLLECTION)
                .whereEqualTo("status", "Chờ duyệt trả")
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                doc.data?.toMutableMap()?.apply {
                    put("id", doc.id)
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi lấy danh sách yêu cầu trả sách: ${e.message}")
            emptyList()
        }
    }

    // Thêm hàm để duyệt yêu cầu trả sách
    suspend fun approveReturnRequest(borrowId: String): Boolean {
        return try {
            // Lấy thông tin mượn sách
            val borrowDoc = db.collection(BORROWS_COLLECTION)
                .document(borrowId)
                .get()
                .await()

            val borrowData = borrowDoc.data
            if (borrowData != null) {
                val bookId = borrowData["bookId"] as? String
                
                // Cập nhật số lượng sách (tăng 1)
                if (bookId != null) {
                    updateBookQuantity(bookId, 1)
                }

                // Cập nhật trạng thái thành "Đã trả"
                db.collection(BORROWS_COLLECTION)
                    .document(borrowId)
                    .update(
                        mapOf(
                            "status" to "Đã trả",
                            "returnDate" to Timestamp(Date())
                        )
                    )
                    .await()

                Log.d("FirebaseManager", "Đã duyệt yêu cầu trả sách thành công")
                true
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("FirebaseManager", "Lỗi khi duyệt yêu cầu trả sách: ${e.message}")
            false
        }
    }
} 