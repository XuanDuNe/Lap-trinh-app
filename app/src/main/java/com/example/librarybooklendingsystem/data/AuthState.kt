package com.example.librarybooklendingsystem.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthState {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val _isLoading = mutableStateOf(false)
    private val _currentUserRole = MutableStateFlow<String?>(null)
    private val _isAdmin = MutableStateFlow(false)
    private val _currentUserShortId = MutableStateFlow<String?>(null)
    private val _isLoggedIn = MutableStateFlow(auth.currentUser != null)
    private lateinit var prefs: SharedPreferences

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUserShortId: StateFlow<String?>
        get() = _currentUserShortId.asStateFlow()

    val isLoading: Boolean
        get() = _isLoading.value

    val currentUserRole: StateFlow<String?>
        get() = _currentUserRole.asStateFlow()

    val isAdmin: StateFlow<Boolean>
        get() = _isAdmin.asStateFlow()

    val isLoggedIn: StateFlow<Boolean>
        get() = _isLoggedIn.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        // Restore saved state
        _currentUserRole.value = prefs.getString("user_role", null)
        _isAdmin.value = prefs.getBoolean("is_admin", false)
        _currentUserShortId.value = prefs.getString("short_id", null)
        _isLoggedIn.value = auth.currentUser != null
    }

    private fun saveAuthState(isAdmin: Boolean, role: String?, shortId: String?) {
        prefs.edit().apply {
            putBoolean("is_admin", isAdmin)
            putString("user_role", role)
            putString("short_id", shortId)
            apply()
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
                                    val shortId = document.getString("short_id")
                                    _currentUserRole.value = role
                                    _isAdmin.value = role == "admin"
                                    _currentUserShortId.value = shortId
                                    _isLoggedIn.value = true
                                    saveAuthState(role == "admin", role, shortId)
                                    _isLoading.value = false
                                    onSuccess()
                                } else {
                                    _currentUserRole.value = "user"
                                    _isAdmin.value = false
                                    _currentUserShortId.value = null
                                    _isLoggedIn.value = true
                                    saveAuthState(false, "user", null)
                                    _isLoading.value = false
                                    onSuccess()
                                }
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                onError(e.message ?: "Failed to get user data")
                            }
                    }
                } else {
                    _isLoading.value = false
                    onError(task.exception?.message ?: "Authentication failed")
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _currentUserRole.value = null
        _isAdmin.value = false
        _currentUserShortId.value = null
        _isLoggedIn.value = false
        saveAuthState(false, null, null)
    }

    fun createAdminAccount(email: String, password: String, context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null) {
                        val userData = hashMapOf(
                            "role" to "admin",
                            "email" to email
                        )
                        db.collection("users").document(user.uid)
                            .set(userData)
                            .addOnSuccessListener {
                                _isAdmin.value = true
                                _currentUserRole.value = "admin"
                                _isLoggedIn.value = true
                                saveAuthState(true, "admin", null)
                                _isLoading.value = false
                                onSuccess()
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                onError(e.message ?: "Failed to create admin account")
                            }
                    }
                } else {
                    _isLoading.value = false
                    onError(task.exception?.message ?: "Failed to create user")
                }
            }
    }
} 