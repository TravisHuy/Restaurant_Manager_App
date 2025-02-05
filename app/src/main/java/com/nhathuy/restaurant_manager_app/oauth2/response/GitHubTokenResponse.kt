package com.nhathuy.restaurant_manager_app.oauth2.response

data class GitHubTokenResponse(
    val access_token:String,
    val token_type:String,
    val scope:String
)
