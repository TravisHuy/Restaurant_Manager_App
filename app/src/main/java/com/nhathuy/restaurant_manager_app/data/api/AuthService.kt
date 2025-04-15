package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.User
import com.nhathuy.restaurant_manager_app.oauth2.request.LoginRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.SignUpRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Defines the authentication API endpoints for user registration, login, and OAuth2 authentication.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
interface AuthService {
    /**
     * Registers a new user with the system.
     *
     * @param request The user registration request
     * @return The response containing the authentication details
     */
    @POST("api/auth/signup")
    suspend fun signup(@Body request : SignUpRequest):Response<AuthResponse>
    /**
     * Logs in an existing user.
     *
     * @param request The login request
     * @return The response containing the authentication details
     */
    @POST("api/auth/login")
    suspend fun login(@Body request:LoginRequest):Response<AuthResponse>
    /**
     * Authenticates a user using OAuth2.
     *
     * @param provider The OAuth2 provider
     * @param code The authorization code
     * @return The response containing the authentication details
     */
    @GET("api/auth/oauth2/callback/{provider}")
    suspend fun oauthCallback(
        @Path("provider") provider: String,
        @Query("code") code: String
    ): Response<AuthResponse>
    /**
     * Sends a logout request to the authentication API.
     *
     * @return A [Response] object with a void body, indicating whether the logout request was successful.a
     */
    @POST("api/auth/logout")
    suspend fun logoutUser(@Header("Authorization") token:String):Response<Void>

    /**
     * Registers a new user with the system with role admin.
     *
     * @param request The user registration request
     * @return The response containing the authentication details
     */
    @POST("api/auth/admin/signup")
    suspend fun registerAdmin(@Body request: SignUpRequest):Response<AuthResponse>

    /**
     * Logs in an existing user with role admin.
     *
     * @param loginRequest The login admin request
     * @return The response containing the authentication details
     */
    @POST("api/auth/admin/login")
    suspend fun loginAdmin(@Body loginRequest:LoginRequest): Response<AuthResponse>

    /**
     * Get all user
     *
     * @return List user
     */
    @GET("api/auth/all/users")
    suspend fun getAllUsers() : Response<List<User>>
}