package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.oauth2.request.LoginRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.SignUpRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.AuthResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthService {
    @POST("api/auth/signup")
    suspend fun signup(@Body request : SignUpRequest):Response<AuthResponse>
    @POST("api/auth/login")
    suspend fun login(@Body request:LoginRequest):Response<AuthResponse>
    @GET("api/auth/oauth2/callback/{provider}")
    suspend fun oauthCallback(
        @Path("provider") provider: String,
        @Query("code") code: String
    ): Response<AuthResponse>
}