package com.nhathuy.restaurant_manager_app.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
/**
 * A class that manages the access token and refresh token
 *
 * @property context The context
 *
 * @version 0.1
 * @since 13-02-2025
 * @author TravisHuy
 */
class TokenManager @Inject constructor(private val context:Context) {

    companion object{
        private const val PREF_NAME = "TokenManager"
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USED_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
    }

    // Create a MasterKey instance
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // Create an instance of EncryptedSharedPreferences
    private val prefs = EncryptedSharedPreferences.create(
        context,
        PREF_NAME,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    /**
     * Save the access token and refresh token to the shared preferences
     *
     * @param accessToken The access token
     * @param refreshToken The refresh token
     * @param userId The user ID
     * @param userRole The user role
     */
    fun saveTokens(accessToken:String , refreshToken:String,userId:String,userRole:String){
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN,accessToken)
            .putString(KEY_REFRESH_TOKEN,refreshToken)
            .putString(KEY_USED_ID,userId)
            .putString(KEY_USER_ROLE,userRole)
            .apply()
    }
    // Get the access token from the shared preferences
    fun getAccessToken() : String? = prefs.getString(KEY_ACCESS_TOKEN,null)
    // Get the refresh token from the shared preferences
    fun getRefreshToken() : String? = prefs.getString(KEY_REFRESH_TOKEN,null)
    // Get the user ID from the shared preferences
    fun getUserId(): String? = prefs.getString(KEY_USED_ID, null)
    // Get the user role from the shared preferences
    fun getUserRole(): String? = prefs.getString(KEY_USER_ROLE, null)
    // Clear the tokens from the shared preferences
    fun clearTokens() {
        prefs.edit().clear().apply()
    }
    // Check if the user is logged in
    fun isUserLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}