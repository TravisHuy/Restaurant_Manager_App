package com.nhathuy.restaurant_manager_app.data.repository

import androidx.compose.animation.core.estimateAnimationDurationMillis
import com.nhathuy.restaurant_manager_app.data.api.NotificationService
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.model.FcmTokenRequest
import com.nhathuy.restaurant_manager_app.data.model.Notification
import com.nhathuy.restaurant_manager_app.oauth2.request.AdminNotificationRequest
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val notificationService: NotificationService,
    private val sessionManager: SessionManager
) {

    /**
     * get notification based on user's role
     */
    suspend fun getNotifications(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        try {
            val userRole = sessionManager.userRole.value
            val response = when (userRole) {
                "ROLE_ADMIN" -> notificationService.getAdminNotification()
                "ROLE_MANAGER" -> notificationService.getManagerNotification()
                "ROLE_EMPLOYEE" -> notificationService.getEmployeeNotification()
                else -> null
            }

            if (response != null && response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                val errorMsg = response?.errorBody()?.string() ?: "Unknown error occurred"
                val errorCode = response?.code() ?: 0
                emit(Resource.Error("Failed to fetch notifications: Code $errorCode - $errorMsg"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getUnreadNotifications(): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        try {
            val userRole = sessionManager.userRole.value
            val response = notificationService.getUnreadNotification(userRole!!)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                emit(Resource.Error("Failed to fetch unread notifications: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun markAsRead(notificationId: String): Resource<List<Notification>> {
        return try {
            val response = notificationService.markAsRead(notificationId)
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: emptyList())
            } else {
                val errorMsg = response.errorBody()?.string() ?: "unknow error"
                Resource.Error("Failed to fetch mark as read notifications: ${response.code()} - $errorMsg")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun markAllAsRead(): Resource<Unit> {
        return try {
            val userRole = sessionManager.userRole.value
            val response = notificationService.markAllAsRead(userRole!!)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Resource.Error("Failed to mark all notifications as read: ${response.code()} - $errorMsg")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

    fun getNotificationForEntity(entityId: String): Flow<Resource<List<Notification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = notificationService.getNotificationForEntity(entityId)
            if (response.isSuccessful) {
                emit(Resource.Success(response.body() ?: emptyList()))
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                emit(Resource.Error("Failed to fetch entity notifications: ${response.code()} - $errorMsg"))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "unknown error"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun createAdminNotification(request: AdminNotificationRequest): Resource<Notification> {
        return try {
            val response = notificationService.createAdminNotification(request)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Empty response body")
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Resource.Error("Failed to create admin notification: ${response.code()} - $errorMsg")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "unknown error")
        }
    }

    suspend fun updateFcmToken(token: String): Resource<Unit> {
        return try {
            val request = FcmTokenRequest(token)
            val response = notificationService.updateFcmToken(request)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                Resource.Error("Failed to update FCM token: ${response.code()} - $errorMsg")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Unknown error")
        }
    }

}