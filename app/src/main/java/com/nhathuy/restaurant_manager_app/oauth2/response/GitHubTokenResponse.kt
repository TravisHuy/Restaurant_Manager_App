package com.nhathuy.restaurant_manager_app.oauth2.response
/**
 * Represents a response to the GitHub access token request.
 * This class is used to map the response body to a Java object.
 *
 * @version 0.1
 * @since 12-02-2025
 * @author TravisHuy
 */
data class GitHubTokenResponse(
    /** The access token of the user */
    val access_token:String,
    /** The token type of the user */
    val token_type:String,
    /** The scope of the user */
    val scope:String
)
