package com.example.librarybooklendingsystem.ui.screens

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object AuthState {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _isLoading = mutableStateOf(false)
    private val _currentUserRole = MutableStateFlow<String?>(null)
    private val _isAdmin = MutableStateFlow(false)
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        // Restore saved state
        _currentUserRole.value = prefs.getString("user_role", null)
        _isAdmin.value = prefs.getBoolean("is_admin", false)
    }

    private fun saveAuthState(role: String?, isAdmin: Boolean) {
        prefs.edit().apply {
            putString("user_role", role)
            putBoolean("is_admin", isAdmin)
            apply()
        }
    }

    val isLoading: Boolean
        get() = _isLoading.value

    val currentUserRole: StateFlow<String?>
        get() = _currentUserRole.asStateFlow()

    val isAdmin: StateFlow<Boolean>
        get() = _isAdmin.asStateFlow()

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun getCurrentUser() = auth.currentUser

    fun updateUserRole(onComplete: (Boolean) -> Unit = {}) {
        _isLoading.value = true
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userRef = db.collection("users").document(currentUser.uid)
            userRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role") ?: "user"
                        _currentUserRole.value = role
                        _isAdmin.value = role == "admin"
                        saveAuthState(role, role == "admin")
                        onComplete(true)
                    } else {
                        _currentUserRole.value = "user"
                        _isAdmin.value = false
                        saveAuthState("user", false)
                        onComplete(false)
                    }
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    _currentUserRole.value = "user"
                    _isAdmin.value = false
                    saveAuthState("user", false)
                    _isLoading.value = false
                    onComplete(false)
                }
        } else {
            _currentUserRole.value = null
            _isAdmin.value = false
            saveAuthState(null, false)
            _isLoading.value = false
            onComplete(false)
        }
    }

    fun signIn(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userRef = db.collection("users").document(user.uid)
                        userRef.get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val role = document.getString("role") ?: "user"
                                    _currentUserRole.value = role
                                    _isAdmin.value = role == "admin"
                                    saveAuthState(role, role == "admin")
                                    onSuccess()
                                } else {
                                    _currentUserRole.value = "user"
                                    _isAdmin.value = false
                                    saveAuthState("user", false)
                                    onSuccess()
                                }
                                _isLoading.value = false
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                onError("Lỗi khi lấy thông tin người dùng: ${e.message}")
                            }
                    }
                } else {
                    _isLoading.value = false
                    onError("Đăng nhập thất bại: ${task.exception?.message}")
                }
            }
    }

    fun signOut(context: Context, onComplete: () -> Unit = {}) {
        _isLoading.value = true
        try {
            auth.signOut()
            _currentUserRole.value = null
            _isAdmin.value = false
            saveAuthState(null, false)
            Toast.makeText(context, "Đăng xuất thành công", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Lỗi đăng xuất: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            _isLoading.value = false
            onComplete()
        }
    }

    fun createUser(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Tạo document user với role mặc định là "user"
                        val userRef = db.collection("users").document(user.uid)
                        userRef.set(mapOf("role" to "user"))
                            .addOnSuccessListener {
                                _currentUserRole.value = "user"
                                _isAdmin.value = false
                                _isLoading.value = false
                                Toast.makeText(context, "Tạo tài khoản thành công", Toast.LENGTH_SHORT).show()
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                onError("Lỗi tạo tài khoản: ${e.message}")
                            }
                    }
                } else {
                    _isLoading.value = false
                    onError(task.exception?.message ?: "Lỗi tạo tài khoản")
                }
            }
    }

    fun createAdminAccount(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        // Tạo document trong collection users với role là admin
                        val userRef = db.collection("users").document(user.uid)
                        val userData = hashMapOf(
                            "role" to "admin",
                            "email" to email,
                            "createdAt" to System.currentTimeMillis()
                        )
                        
                        userRef.set(userData)
                            .addOnSuccessListener {
                                // Tạo document trong collection admin
                                val adminRef = db.collection("admin").document(user.uid)
                                val adminData = hashMapOf(
                                    "email" to email,
                                    "createdAt" to System.currentTimeMillis(),
                                    "isActive" to true
                                )
                                
                                adminRef.set(adminData)
                                    .addOnSuccessListener {
                                        _currentUserRole.value = "admin"
                                        _isAdmin.value = true
                                        _isLoading.value = false
                                        Toast.makeText(context, "Tài khoản admin đã được tạo thành công!", Toast.LENGTH_LONG).show()
                                        onSuccess()
                                    }
                                    .addOnFailureListener { e ->
                                        _isLoading.value = false
                                        onError("Lỗi khi tạo thông tin admin: ${e.message}")
                                    }
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                onError("Lỗi khi tạo quyền admin: ${e.message}")
                            }
                    }
                } else {
                    _isLoading.value = false
                    onError(task.exception?.message ?: "Lỗi tạo tài khoản admin")
                }
            }
    }

    fun setAdminRole(userId: String, email: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        
        // Cập nhật role trong collection users
        val userRef = db.collection("users").document(userId)
        val userData = hashMapOf(
            "role" to "admin",
            "email" to email,
            "updatedAt" to System.currentTimeMillis()
        )
        
        userRef.set(userData)
            .addOnSuccessListener {
                // Tạo hoặc cập nhật document trong collection admin
                val adminRef = db.collection("admin").document(userId)
                val adminData = hashMapOf(
                    "email" to email,
                    "createdAt" to System.currentTimeMillis(),
                    "isActive" to true
                )
                
                adminRef.set(adminData)
                    .addOnSuccessListener {
                        _currentUserRole.value = "admin"
                        _isAdmin.value = true
                        _isLoading.value = false
                        Toast.makeText(context, "Đã phân quyền admin thành công!", Toast.LENGTH_LONG).show()
                        onSuccess()
                    }
                    .addOnFailureListener { e ->
                        _isLoading.value = false
                        onError("Lỗi khi tạo thông tin admin: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                onError("Lỗi khi phân quyền admin: ${e.message}")
            }
    }

    fun checkAdminRole(userId: String, onResult: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        userRef.get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val role = document.getString("role") ?: "user"
                    _currentUserRole.value = role
                    _isAdmin.value = role == "admin"
                    onResult(role == "admin")
                } else {
                    _currentUserRole.value = "user"
                    _isAdmin.value = false
                    onResult(false)
                }
            }
            .addOnFailureListener {
                _currentUserRole.value = "user"
                _isAdmin.value = false
                onResult(false)
            }
    }
} 