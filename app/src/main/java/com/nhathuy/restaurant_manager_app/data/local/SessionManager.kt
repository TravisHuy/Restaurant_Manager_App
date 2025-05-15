package com.nhathuy.restaurant_manager_app.data.local

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.nhathuy.restaurant_manager_app.service.RestaurantFirebaseMessagingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * Manages user session and authentication states
 */
class SessionManager @Inject constructor(
    private val tokenManager: TokenManager,
    private val context: Context
) {
    /**
     * The isLoggedIn StateFlow.
     * This StateFlow is used to store the login state of the user.
     */
    private val _isLoggedIn = MutableStateFlow(tokenManager.isUserLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    /**
     * The userRole StateFlow
     * This StateFlow is used to store the role of the user
     */
    private val _userRole = MutableStateFlow(tokenManager.getUserRole())
    val userRole: StateFlow<String?> = _userRole

    /**
     * Update the login state and register FCM token if logged in
     */
    fun updateLoginState(isLoggedIn: Boolean) {
        _isLoggedIn.value = isLoggedIn

        // Register FCM token when logging in
        if (isLoggedIn) {
            registerFcmToken()
        }
    }

    /**
     * Update the user role
     */
    fun updateUserRole(role: String?) {
        _userRole.value = role
    }

    /**
     * Get the current user ID from token
     */
    fun getCurrentUserId(): String? = tokenManager.getUserId()

    /**
     * Logout the user and clear tokens
     */
    fun logout() {
        tokenManager.clearTokens()
        _isLoggedIn.value = false
        _userRole.value = null
    }

    /**
     * Check authentication state and update states
     */
    fun checkAuthState() {
        val isLoggedIn = tokenManager.isUserLoggedIn()
        updateLoginState(isLoggedIn)
        if (isLoggedIn) {
            updateUserRole(tokenManager.getUserRole())
            registerFcmToken() // Register token on app startup if logged in
        }
    }

    /**
     * Register FCM token with server
     */
    private fun registerFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                RestaurantFirebaseMessagingService.registerToken(context, token)
            }
        }
    }
}