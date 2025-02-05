package com.nhathuy.restaurant_manager_app.oauth2.response

data class AuthResponse(
    val token:String,
    val message:String,
    val type:String = "Bearer",
    val refreshToken:String,
    val email:String,
    val role:String,
    val name:String,
    val id:String
)
