package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.AuthService
import com.nhathuy.restaurant_manager_app.oauth2.request.LoginRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.SignUpRequest
import javax.inject.Inject

class AuthRepository @Inject constructor(private val authService: AuthService) {
    suspend fun login(request: LoginRequest) = authService.login(request)
    suspend fun register(request:SignUpRequest) = authService.signup(request)
    suspend fun oauthCallback(provider:String ,code:String) = authService.oauthCallback(provider,code)
}