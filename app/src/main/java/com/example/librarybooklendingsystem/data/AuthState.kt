package com.example.librarybooklendingsystem.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
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
    private val _isLoading = MutableStateFlow(false)
    private val _currentUserRole = MutableStateFlow<String?>(null)
    private val _isAdmin = MutableStateFlow(false)
    private val _currentUserShortId = MutableStateFlow<String?>(null)
    private lateinit var prefs: SharedPreferences

    val currentUserId: String?
        get() = auth.currentUser?.uid

    val currentUserShortId: StateFlow<String?>
        get() = _currentUserShortId.asStateFlow()

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        _currentUserRole.value = prefs.getString("user_role", null)
        _isAdmin.value = prefs.getBoolean("is_admin", false)
        _currentUserShortId.value = prefs.getString("user_short_id", null)
    }

    private fun saveAuthState(role: String?, isAdmin: Boolean, shortId: String? = null) {
        prefs.edit().apply {
            putString("user_role", role)
            putBoolean("is_admin", isAdmin)
            putString("user_short_id", shortId)
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

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun signOut(context: Context) {
        auth.signOut()
        _currentUserRole.value = null
        _isAdmin.value = false
        _currentUserShortId.value = null
        saveAuthState(null, false, null)
    }

    fun updateUserRole(onComplete: (Boolean) -> Unit = {}) {
        _isLoading.value = true
        val currentUser = auth.currentUser
        if (currentUser != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val userDoc = db.collection("users").document(currentUser.uid).get().await()
                    if (userDoc.exists()) {
                        val role = userDoc.getString("role") ?: "user"
                        val shortId = userDoc.getString("id")
                        
                        _currentUserRole.value = role
                        _isAdmin.value = role == "admin"
                        _currentUserShortId.value = shortId
                        
                        saveAuthState(role, role == "admin", shortId)
                        _isLoading.value = false
                        withContext(Dispatchers.Main) {
                            onComplete(true)
                        }
                    } else {
                        _currentUserRole.value = "user"
                        _isAdmin.value = false
                        _currentUserShortId.value = null
                        saveAuthState("user", false)
                        _isLoading.value = false
                        withContext(Dispatchers.Main) {
                            onComplete(false)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("AuthState", "Error updating user role: ${e.message}")
                    _currentUserRole.value = "user"
                    _isAdmin.value = false
                    _currentUserShortId.value = null
                    saveAuthState("user", false)
                    _isLoading.value = false
                    withContext(Dispatchers.Main) {
                        onComplete(false)
                    }
                }
            }
        } else {
            _currentUserRole.value = null
            _isAdmin.value = false
            _currentUserShortId.value = null
            saveAuthState(null, false)
            _isLoading.value = false
            onComplete(false)
        }
    }
} 