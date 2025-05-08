package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.AdminNotificationService
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.data.model.WebSocketConnectionState
import com.nhathuy.restaurant_manager_app.resource.Resource
import com.nhathuy.restaurant_manager_app.service.RestaurantWebSocketClient
import com.nhathuy.restaurant_manager_app.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for handling notifications
 *
 * @property notificationService The service for API calls related to notifications
 * @property webSocketClient The WebSocket client for real-time notifications
 * @version 0.1
 * @since 08-05-2025
 * @author TravisHuy
 */
@Singleton
class NotificationRepository @Inject constructor(
    private val notificationService: AdminNotificationService,
    private val webSocketClient: RestaurantWebSocketClient
) {
    /**
     * initializeWebsocket the websocket connection returns the connections state
     */
    fun initializeWebSocket() : Flow<WebSocketConnectionState>{
        webSocketClient.connect(Constants.AUTH_URL)
        return webSocketClient.connectionState
    }

    /**
     * Returns a flow of real-time notifications
     */
    fun getRealtimeNotifications(): Flow<AdminNotification> {
        return webSocketClient.notificationFlow
    }

    /**
     * Loads all notifications from the API
     */
    fun loadNotifications(): Flow<Resource<List<AdminNotification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = notificationService.getAllNotification()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("Empty response body"))
            } else {
                emit(Resource.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to load notifications: ${e.message}"))
        }
    }

    /**
     * get unread notification from the api
     */
    fun getUnreadNotifications() : Flow<Resource<List<AdminNotification>>> = flow {
        emit(Resource.Loading())
        try {
            val response = notificationService.getUnreadNotifications()
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                }?:emit(Resource.Error("Error: ${response.code()} - ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Failed to load unread notifications ${e.message}"))
        }
    }

    /**
     * Marks a notification as read
     */
    fun markAsRead(id: String): Flow<Resource<Unit>> = flow  {
        emit(Resource.Loading())
        try {
            val response = notificationService.markAsRead(id)
            if (response.isSuccessful) {
                emit(Resource.Success(Unit))
            } else {
                emit(Resource.Error("Error: ${response.code()} - ${response.message()}"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Failed to mark notification as read: ${e.message}"))
        }
    }
}