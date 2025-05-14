package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.NotificationService
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.model.FcmTokenRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FcmTokenRepository @Inject constructor(private val notificationService: NotificationService,
                                             private val sessionManager: SessionManager
) {
    /**
     * Register FCm token with the server
     */
    suspend fun registerFcmToken(token:String) : Result<Unit> = withContext(Dispatchers.IO){
        try {
            if(!sessionManager.isLoggedIn.value){
                return@withContext Result.failure(Exception("user logged in"))
            }
            val response = notificationService.updateFcmToken(FcmTokenRequest(token))
            if(response.isSuccessful){
                Result.success(Unit)
            }
            else{
                Result.failure(Exception("Failed to register FCM token: ${response.message()}"))
            }
        }
        catch (e:Exception){
            Result.failure(e)
        }
    }
}