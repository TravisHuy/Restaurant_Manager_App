package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.repository.AuthRepository
import com.nhathuy.restaurant_manager_app.oauth2.request.LoginRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.SignUpRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.AuthResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(private val repository: AuthRepository):ViewModel() {

    private val _loginResult = MutableLiveData<Resource<AuthResponse>>()
    val loginResult : LiveData<Resource<AuthResponse>> = _loginResult


    private val _registerResult= MutableLiveData<Resource<AuthResponse>>()
    val registerResult : LiveData<Resource<AuthResponse>> = _registerResult

    private val _oauthResult= MutableLiveData<Resource<AuthResponse>>()
    val oauthResult : LiveData<Resource<AuthResponse>> = _oauthResult

    fun login(email:String, password:String){
        viewModelScope.launch {
            _loginResult.value = Resource.Loading()
            try {
                val response = repository.login(LoginRequest(email, password))
                if(response.isSuccessful){
                    _loginResult.value = Resource.Success(response.body()!!)
                }
                else{
                    _loginResult.value = Resource.Error("Login failed: ${response.message()}")
                }
            }catch (e:Exception){
                _loginResult.value = Resource.Error("Login error: ${e.message}")
            }
        }
    }

    fun register(name:String,email:String,password:String,phoneNumber:String,address:String,avatar:String=""){
        viewModelScope.launch {
            _registerResult.value = Resource.Loading()
            try {
                val request = SignUpRequest(name, email, password, phoneNumber, address, avatar)
                val response = repository.register(request)
                if(response.isSuccessful){
                    _registerResult.value = Resource.Success(response.body()!!)
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

    fun handleOAuthCallback(provider:String,code:String){
        viewModelScope.launch {
            _oauthResult.value = Resource.Loading()
            try {
                val response = repository.oauthCallback(provider, code)
                if (response.isSuccessful) {
                    _oauthResult.value = Resource.Success(response.body()!!)
                } else {
                    _oauthResult.value = Resource.Error("OAuth login failed: ${response.message()}")
                }
            } catch (e: Exception) {
                _oauthResult.value = Resource.Error("OAuth error: ${e.message}")
            }
        }
    }
}