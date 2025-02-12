package com.nhathuy.restaurant_manager_app.oauth2.info
/**
 * Represents the user information from the Google OAuth2 provider.
 * This data class is used to store the user information from the GitHub OAuth2 provider.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
data class GoogleUserInfo(
    val id:String,
    val email:String,
    val name:String,
    val imageUrl:String?
)
