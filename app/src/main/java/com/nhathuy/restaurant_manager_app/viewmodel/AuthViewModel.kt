package com.nhathuy.restaurant_manager_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.data.repository.AuthRepository
import com.nhathuy.restaurant_manager_app.oauth2.request.LoginRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.SignUpRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.AuthResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject
/**
 * AuthViewModel for interacting with [User] documents.
 *
 * @version 0.1
 * @return 07-02-2025
 * @author TravisHuy
 */
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager,
    private val sessionManger: SessionManager
):ViewModel() {
    /**
     * The loginResult LiveData.
     * This LiveData is used to store the result of the login operation.
     */
    private val _loginResult = MutableLiveData<Resource<AuthResponse>>()
    val loginResult : LiveData<Resource<AuthResponse>> = _loginResult

    /**
     * The registerResult LiveData.
     * This LiveData is used to store the result of the registration operation.
     */
    private val _registerResult= MutableLiveData<Resource<AuthResponse>>()
    val registerResult : LiveData<Resource<AuthResponse>> = _registerResult
    /**
     * The oauthResult LiveData.
     * This LiveData is used to store the result of the OAuth2 authentication operation.
     */
    private val _oauthResult= MutableLiveData<Resource<AuthResponse>>()
    val oauthResult : LiveData<Resource<AuthResponse>> = _oauthResult

    /**
     * The logoutResult LiveData.
     * This LiveData is used to store the result of the logout operation.
     */
    private val _logoutResult = MutableLiveData<Resource<Unit>>()
    val logoutResult : LiveData<Resource<Unit>> = _logoutResult

    /**
     * The login function.
     * This function is used to perform the login operation.
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun login(email:String, password:String){
        viewModelScope.launch {
            _loginResult.value = Resource.Loading()
            try {
                val response = repository.login(LoginRequest(email, password))
                if(response.isSuccessful){
                    response.body()?.let {
                        authResponse ->

                        tokenManager.saveTokens(
                            accessToken = authResponse.token,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.id,
                            userRole = authResponse.role)


                        Log.d("AuthViewModel","login: ${authResponse.token}")

                        sessionManger.updateLoginState(true)
                        sessionManger.updateUserRole(authResponse.role)

                        _loginResult.value = Resource.Success(authResponse)
                    }
                }
                else{
                    _loginResult.value = Resource.Error("Login failed: ${response.message()}")
                }
            }catch (e:Exception){
                _loginResult.value = Resource.Error("Login error: ${e.message}")
            }
        }
    }
    /**
     * The register function.
     * This function is used to perform the registration operation.
     */
    fun register(name:String,email:String,password:String,phoneNumber:String,address:String,avatar:String=""){
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            try {
                val request = SignUpRequest(name, email, password, phoneNumber, address, avatar)
                val response = repository.register(request)
                if(response.isSuccessful){
                    response.body()?.let {
                        authResponse ->

                        tokenManager.saveTokens(
                            accessToken = authResponse.token,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.id,
                            userRole = authResponse.role)

                        _registerResult.value = Resource.Success(response.body()!!)
                    }
                }
                else{
                    _registerResult.value = Resource.Error("Registration failed: ${response.message()}")
                }
            }
            catch (e:Exception){
                _registerResult.value = Resource.Error("Registration error: ${e.message}")
            }
        }
    }
    /**
     * The handleOAuthCallback function.
     * This function is used to handle the OAuth2 callback.
     * @param provider The OAuth2 provider.
     * @param code The authorization code.
     */
    fun handleOAuthCallback(provider:String,code:String){
        viewModelScope.launch {
            _oauthResult.value = Resource.Loading()
            try {
                val response = repository.oauthCallback(provider, code)
                if (response.isSuccessful) {
                    response.body()?.let { authResponse ->

                        tokenManager.saveTokens(
                            accessToken = authResponse.token,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.id,
                            userRole = authResponse.role
                        )

                        sessionManger.updateLoginState(true)
                        sessionManger.updateUserRole(authResponse.role)

                        _oauthResult.value = Resource.Success(response.body()!!)
                    }

                } else {
                    _oauthResult.value = Resource.Error("OAuth login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _oauthResult.value = Resource.Error("OAuth error: ${e.message}")
            }
        }
    }

    /**
     * The logout function.
     * This function is used to perform the logout operation.
     */
    fun logout(){
        viewModelScope.launch {
            _logoutResult.value = Resource.Loading()
            try {
                tokenManager.clearTokens()
                sessionManger.logout()
                _logoutResult.value = Resource.Success(Unit)
            }
            catch (e:Exception){
                _logoutResult.value = Resource.Error("Logout error: ${e.message}")
            }
        }
    }

    /**
     * The isLoggedIn function.
     * This function is used to check if the user is logged in.
     * @return True if the user is logged in, false otherwise.
     */
    fun isLoggedIn():Boolean{
        return tokenManager.getAccessToken() != null
    }
}