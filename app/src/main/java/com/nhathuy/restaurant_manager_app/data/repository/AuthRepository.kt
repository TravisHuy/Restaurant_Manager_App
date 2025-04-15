package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.AuthService
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.oauth2.request.LoginRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.SignUpRequest
import retrofit2.Response
import javax.inject.Inject
/**
 * The repository class for the authentication service.
 * This class is used to interact with the authentication service to perform login, registration, and OAuth2 authentication.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
class AuthRepository @Inject constructor(private val authService: AuthService,
                                         private val tokenManager: TokenManager) {
    suspend fun login(request: LoginRequest) = authService.login(request)
    suspend fun register(request:SignUpRequest) = authService.signup(request)
    suspend fun oauthCallback(provider:String ,code:String) = authService.oauthCallback(provider,code)

    suspend fun logoutUser() : Response<Void> {
        val token = tokenManager.getAccessToken()
        return authService.logoutUser("Bear $token")
    }

    suspend fun loginAdmin(request: LoginRequest) = authService.loginAdmin(request)
    suspend fun registerAdmin(request: SignUpRequest) = authService.registerAdmin(request)
    suspend fun getAllUsers() = authService.getAllUsers()
}