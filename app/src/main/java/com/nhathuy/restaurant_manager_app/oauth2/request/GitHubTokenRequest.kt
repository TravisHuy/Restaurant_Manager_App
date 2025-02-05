package com.nhathuy.restaurant_manager_app.oauth2.request

data class GitHubTokenRequest(
    val code:String,
    val client_id:String,
    val client_secret:String,
    val redirect_uri:String
)
