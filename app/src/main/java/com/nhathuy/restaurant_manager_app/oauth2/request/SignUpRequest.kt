package com.nhathuy.restaurant_manager_app.oauth2.request

data class SignUpRequest(
    val name:String,
    val email:String,
    val password:String,
    val phoneNumber:String,
    val address:String,
    val avatar:String
)
