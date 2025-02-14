package com.nhathuy.restaurant_manager_app.data.model

/**
 * Represents a user entity in the system.
 *
 * @version 0.1
 * @since 03-02-2025
 * @author TravisHuy
 */
data class User(
    // The unique identifier of the user
    val id:String,
    // The name of the user
    val name:String,
    // The email of the user
    val email:String,
    // The password of the user
    val password:String,
    // The phone number of the user
    val phoneNumber:String,
    // The address of the user
    val address:String,
    // The avatar of the user
    val avatar:String,
    // The role of the user
    val role:Set<Role>,
    // The provider of the user
    val provider: AuthProvider,
    // User's provider id
    val providerId:String,
    // The authorities of the user
    val authorities:Set<String>
)