package com.nhathuy.restaurant_manager_app.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.data.model.User
import com.nhathuy.restaurant_manager_app.data.repository.AuthRepository
import com.nhathuy.restaurant_manager_app.oauth2.request.LoginRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.SignUpRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.AuthResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import retrofit2.HttpException
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
     * The loginResult LiveData.
     * This LiveData is used to store the result of the login operation.
     */
    private val _loginAdminResult = MutableLiveData<Resource<AuthResponse>>()
    val loginAdminResult : LiveData<Resource<AuthResponse>> = _loginAdminResult

    /**
     * The registerResult LiveData.
     * This LiveData is used to store the result of the registration operation.
     */
    private val _registerAdminResult= MutableLiveData<Resource<AuthResponse>>()
    val registerAdminResult : LiveData<Resource<AuthResponse>> = _registerAdminResult

    /**
     * The allUsers LiveData.
     * This LiveData is used to store the result of the list user operation.
     */
    private val _allUsers = MutableLiveData<Resource<List<User>>>()
    val allUsers : LiveData<Resource<List<User>>> = _allUsers

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
                val response  = repository.logoutUser()
                if(response.isSuccessful){
                    //clear local tokens regardless of the API response
                    tokenManager.clearTokens()
                    sessionManger.logout()
                    _logoutResult.value = Resource.Success(Unit)
                    Log.d("AuthViewModel","Login Successfully")
                }
                else{
                    tokenManager.clearTokens()
                    sessionManger.logout()
                    _logoutResult.value = Resource.Error("Backend logout failed but local logout completed")
                }
            }
            catch (e:Exception){
                tokenManager.clearTokens()
                sessionManger.logout()
                _logoutResult.value = Resource.Error("Logout error: ${e.message}, but tokens cleared locally")
                Log.e("AuthViewModel", "Logout error: ${e.message}", e)
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


    /**
     * The login function.
     * This function is used to perform the login operation.
     * @param email The email of the user.
     * @param password The password of the user.
     */
    fun loginAdmin(email:String, password:String){
        viewModelScope.launch {
            _loginAdminResult.value = Resource.Loading()
            try {
                val response = repository.loginAdmin(LoginRequest(email, password))
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

                        _loginAdminResult.value = Resource.Success(authResponse)
                    }
                }
                else{
                    _loginAdminResult.value = Resource.Error("Login failed: ${response.message()}")
                }
            }
            catch (e:HttpException){
                val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                _loginAdminResult.value = Resource.Error("Login error: $errorBody")
            }
            catch (e:Exception){
                _loginAdminResult.value = Resource.Error("Login error: ${e.message}")
            }
        }
    }
    /**
     * The register function.
     * This function is used to perform the registration operation.
     */
    fun registerAdmin(name:String,email:String,password:String,phoneNumber:String,address:String,avatar:String=""){
        viewModelScope.launch {
            _registerAdminResult.value = Resource.Loading()
            try {
                val request = SignUpRequest(name, email, password, phoneNumber, address, avatar)
                val response = repository.registerAdmin(request)
                if(response.isSuccessful){
                    response.body()?.let {
                            authResponse ->

                        tokenManager.saveTokens(
                            accessToken = authResponse.token,
                            refreshToken = authResponse.refreshToken,
                            userId = authResponse.id,
                            userRole = authResponse.role)

                        _registerAdminResult.value = Resource.Success(response.body()!!)
                    }
                }
                else{
                    _registerAdminResult.value = Resource.Error("Registration failed: ${response.message()}")
                }
            }
            catch (e:HttpException){
                val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                _registerAdminResult.value = Resource.Error("Registration error: $errorBody")
            }
            catch (e:Exception){
                _registerAdminResult.value = Resource.Error("Registration error: ${e.message}")
            }
        }
    }

    fun getAllUsers(){
        viewModelScope.launch {
            _allUsers.value = Resource.Loading()
            try {
                val response = repository.getAllUsers()
                if(response.isSuccessful){
                    response.body()?.let {
                        _allUsers.value = Resource.Success(response.body()!!)
                    }
                }
                else{
                    _allUsers.value = Resource.Error("get list user failed")
                }
            }
            catch (e:HttpException){
                val errorBody = e.response()?.errorBody()?.string() ?: "Unknown error"
                _allUsers.value = Resource.Error("Get all users error: $errorBody")
            }
            catch (e:Exception){
                _allUsers.value = Resource.Error("Get all users error: ${e.message}")
            }
        }
    }
}