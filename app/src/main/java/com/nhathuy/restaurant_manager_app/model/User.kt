package com.nhathuy.restaurant_manager_app.model

data class User(
    val id:String,
    val name:String,
    val email:String,
    val password:String,
    val phoneNumber:String,
    val address:String,
    val avatar:String,
    val role:String,
    val provider:AuthProvider,
    val providerId:String,
    val authorities:Set<String>
)