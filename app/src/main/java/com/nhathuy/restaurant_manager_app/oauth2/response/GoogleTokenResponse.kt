package com.nhathuy.restaurant_manager_app.oauth2.response

data class GoogleTokenResponse(
    val access_token:String,
    val id_token:String,
    val token_type:String,
    val expires_in:String
)
