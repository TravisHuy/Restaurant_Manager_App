package com.nhathuy.restaurant_manager_app.util

/**
 * Object containing all the constants used in the application.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
object Constants {
    /**
     * Base URL for authentication API.
     * "10.0.2.2" is used to access the localhost of the development machine from an Android emulator.
     */
    const val AUTH_URL="http://10.0.2.2:8080/"
    const val GITHUB_AUTH_URL = "https://github.com/login/oauth/authorize"
    const val GITHUB_SCOPE = "user:email"
    const val GITHUB_REDIRECT_URI = "http://localhost:8080/api/auth/oauth2/callback/github"

    const val PREFS_NAME = "LoginPrefs"
    const val KEY_EMAIL = "email"
    const val KEY_PASSWORD = "password"
    const val KEY_REMEMBER_ME = "remember_me"


    const val IMAGE_URL = "${AUTH_URL}api/menu-items/%s/image"
    const val REQUEST_CODE_CREATE_ORDER = 1001
    const val REQUEST_CODE_CREATE_ORDER_ITEM=1002
    const val REQUEST_CODE_ORDER_PAYMENT=1003

}