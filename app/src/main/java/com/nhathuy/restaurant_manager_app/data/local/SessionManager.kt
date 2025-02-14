package com.nhathuy.restaurant_manager_app.data.local

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class SessionManager @Inject constructor(private val tokenManager: TokenManager){
    private val _isLoggedIn = MutableStateFlow(tokenManager.isUserLoggedIn())
    val isLoggedIn:StateFlow<Boolean> = _isLoggedIn

    private val _userRole = MutableStateFlow(tokenManager.getUserRole())
    val userRole:StateFlow<String?> = _userRole

    fun updateLoginState(isLoggedIn:Boolean){
        _isLoggedIn.value = isLoggedIn
    }

    fun updateUserRole(role:String?){
        _userRole.value = role
    }

    fun getCurrentUserId() : String? = tokenManager.getUserId()

    fun logout(){
        tokenManager.clearTokens()
        _isLoggedIn.value = false
        _userRole.value = null
    }

    fun checkAuthState(){
        val isLoggedIn = tokenManager.isUserLoggedIn()
        updateLoginState(isLoggedIn)
        if(isLoggedIn){
            updateUserRole(tokenManager.getUserRole())
        }
    }
}