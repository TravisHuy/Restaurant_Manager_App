package com.nhathuy.restaurant_manager_app.oauth2.response
/**
 * Represents the response from the authentication server.
 * This data class is used to store the response from the authentication server.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
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
