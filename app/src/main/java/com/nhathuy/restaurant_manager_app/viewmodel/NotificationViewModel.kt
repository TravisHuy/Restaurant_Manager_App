package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.Notification
import com.nhathuy.restaurant_manager_app.data.model.NotificationType
import com.nhathuy.restaurant_manager_app.data.repository.FcmTokenRepository
import com.nhathuy.restaurant_manager_app.data.repository.NotificationRepository
import com.nhathuy.restaurant_manager_app.oauth2.request.AdminNotificationRequest
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationViewModel @Inject constructor(private val notificationRepository: NotificationRepository,
                                                private val fcmTokenRepository: FcmTokenRepository
):ViewModel() {

    private val _notifications = MutableStateFlow<Resource<List<Notification>>>(Resource.Loading())
    val notifications : StateFlow<Resource<List<Notification>>> = _notifications

    private val _unreadNotifications = MutableStateFlow<Resource<List<Notification>>>(Resource.Loading())
    val unreadNotifications : StateFlow<Resource<List<Notification>>> = _unreadNotifications

    private val _entityNotifications = MutableStateFlow<Resource<List<Notification>>>(Resource.Loading())
    val entityNotifications : StateFlow<Resource<List<Notification>>> = _entityNotifications

    // State for notification operation status
    private val _operationStatus = MutableStateFlow<Resource<Unit>>(Resource.Success(Unit))
    val operationStatus: StateFlow<Resource<Unit>> = _operationStatus.asStateFlow()

    // State for newly created notification
    private val _createdNotification = MutableStateFlow<Resource<Notification>?>(null)
    val createdNotification: StateFlow<Resource<Notification>?> = _createdNotification.asStateFlow()


    init {
        loadNotifications()
        loadUnreadNotifications()
    }

    /**
     * Load all notifications for the current user based on their role
     */
    fun loadNotifications(){
        viewModelScope.launch {
            notificationRepository.getNotifications().collectLatest {
                result ->
                _notifications.value = result
            }
        }
    }

    /**
     * Load only unread notifications for the current user
     */
    fun loadUnreadNotifications() {
        viewModelScope.launch {
            notificationRepository.getUnreadNotifications().collectLatest { result ->
                _unreadNotifications.value = result
            }
        }
    }

    /**
     * Load notifications for a specific entity
     * @param entityId ID of the entity to get notifications for
     */
    fun loadEntityNotifications(entityId: String) {
        viewModelScope.launch {
            notificationRepository.getNotificationForEntity(entityId).collectLatest { result ->
                _entityNotifications.value = result
            }
        }
    }

    /**
     * Mark a specific notification as read
     * @param notificationId ID of the notification to mark as read
     */
    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val result = notificationRepository.markAsRead(notificationId)
            if (result is Resource.Success) {
                // Refresh notifications after marking as read
                loadNotifications()
                loadUnreadNotifications()
            }
        }
    }

    /**
     * Mark all notifications as read for the current user
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val result = notificationRepository.markAllAsRead()
            _operationStatus.value = result
            if (result is Resource.Success) {
                // Refresh notifications after marking all as read
                loadNotifications()
                loadUnreadNotifications()
            }
        }
    }

    /**
     * Create a new admin notification
     * @param title Title of the notification
     * @param message Content of the notification
     * @param type Type of notification
     * @param relateId ID of the related entity
     * @param recipientRoles List of roles that should receive this notification
     */
    fun createAdminNotification(
        title: String,
        message: String,
        type: NotificationType,
        relateId: String,
        recipientRoles: List<String>
    ) {
        viewModelScope.launch {
            _createdNotification.value = Resource.Loading()
            val request = AdminNotificationRequest(
                title = title,
                message = message,
                type = type,
                relateId = relateId,
                recipientRoles = recipientRoles
            )
            val result = notificationRepository.createAdminNotification(request)
            _createdNotification.value = result
        }
    }

    /**
     * Register FCM token for push notifications
     * @param token Firebase Cloud Messaging token to register
     */
    fun registerFcmToken(token: String) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val result = fcmTokenRepository.registerFcmToken(token)
            _operationStatus.value = when {
                result.isSuccess -> Resource.Success(Unit)
                else -> Resource.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }

    /**
     * Update FCM token if needed (using NotificationRepository)
     * @param token Firebase Cloud Messaging token to update
     */
    fun updateFcmToken(token: String) {
        viewModelScope.launch {
            _operationStatus.value = Resource.Loading()
            val result = notificationRepository.updateFcmToken(token)
            _operationStatus.value = result
        }
    }

    /**
     * Reset operation status to initial state
     */
    fun resetOperationStatus() {
        _operationStatus.value = Resource.Success(Unit)
    }

    /**
     * Reset created notification state
     */
    fun resetCreatedNotification() {
        _createdNotification.value = null
    }

}