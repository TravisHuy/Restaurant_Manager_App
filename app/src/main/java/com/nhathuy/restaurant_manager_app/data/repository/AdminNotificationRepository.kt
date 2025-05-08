package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.AdminNotificationService
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AdminNotificationRepository @Inject constructor(private val adminNotificationService: AdminNotificationService) {

    suspend fun loadNotifications(): Flow<Resource<List<AdminNotification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = adminNotificationService.getAllNotification()
            if(response.isSuccessful){
                emit(Resource.Success(response.body()!!))
            }
            else{
                emit(Resource.Error("Failed to get all notifications: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Error : ${e.message}"))
        }
    }
    suspend fun getUnreadNotifications(): Flow<Resource<List<AdminNotification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = adminNotificationService.getUnreadNotifications()
            if(response.isSuccessful){
                emit(Resource.Success(response.body()!!))
            }
            else{
                emit(Resource.Error("Failed to get all unread notifications: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Error : ${e.message}"))
        }
    }

    suspend fun markAsRead(id: String): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
        try {
            val response = adminNotificationService.markAsRead(id)
            if(response.isSuccessful){
                emit(Resource.Success(Unit))
            }
            else{
                emit(Resource.Error("Error code: ${response.code()} - ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Exception: ${e.localizedMessage ?: "Unknown error"}"))
        }
    }.flowOn(Dispatchers.IO)
}