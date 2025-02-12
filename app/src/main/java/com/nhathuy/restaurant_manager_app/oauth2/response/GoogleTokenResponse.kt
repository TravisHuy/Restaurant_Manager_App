package com.nhathuy.restaurant_manager_app.oauth2.response

/**
 * Represents a response to the Google access token request.
 * This class is used to map the response body to a Java object.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
data class GoogleTokenResponse(
    val access_token:String,
    val id_token:String,
    val token_type:String,
    val expires_in:String
)
