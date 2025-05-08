package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.data.model.WebSocketConnectionState
import com.nhathuy.restaurant_manager_app.data.repository.NotificationRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationViewModel @Inject constructor(private val notificationRepository: NotificationRepository):ViewModel() {

    private val _notificationState = MutableLiveData<Resource<List<AdminNotification>>>()
    val notificationState : LiveData<Resource<List<AdminNotification>>> = _notificationState

    private val _unreadNotificationsState = MutableLiveData<Resource<List<AdminNotification>>>()
    val unreadNotificationsState: LiveData<Resource<List<AdminNotification>>> = _unreadNotificationsState

    private val _markAsReadState = MutableLiveData<Resource<Unit>>()
    val markAsReadState: LiveData<Resource<Unit>> = _markAsReadState

    // websocket connection state
    private val _connectionState = MutableLiveData<WebSocketConnectionState>()
    val connectionState: LiveData<WebSocketConnectionState> = _connectionState

    //real-time notifications received from websocket
    private val _realtimeNotification = MutableLiveData<AdminNotification>()
    val realTimeNotification : LiveData<AdminNotification> = _realtimeNotification

    /**
     * Initializes the model
     */
    init {
        loadAllNotifications()
        loadUnreadNotifications()
        initializeWebSocket()
    }

    /**
     * Initializes websocket connection and starts collectings notifications
     */
    fun initializeWebSocket(){
        viewModelScope.launch {
            notificationRepository.initializeWebSocket().collect {
                state ->
                _connectionState.value = state
            }
        }

        viewModelScope.launch {
            notificationRepository.getRealtimeNotifications().collect {
                notification ->
                _realtimeNotification.value = notification

                loadAllNotifications()
                loadUnreadNotifications()
            }
        }
    }

    /**
     * load all notifications from the repository
     */
    fun loadAllNotifications(){
        viewModelScope.launch {
            notificationRepository.loadNotifications().collectLatest { result ->
                _notificationState.value = result
            }
        }
    }

    /**
     * loads unread notifications from the repository
     */
    fun loadUnreadNotifications(){
        viewModelScope.launch {
            notificationRepository.getUnreadNotifications().collectLatest { result ->
                _unreadNotificationsState.value = result
            }
        }
    }

    /**
     * Marks a notification as read
     */
    fun markNotificationRead(id:String){
        viewModelScope.launch {
            notificationRepository.markAsRead(id).collectLatest {
                result ->
                _markAsReadState.value = result

                if(result is Resource.Success){
                    loadAllNotifications()
                    loadUnreadNotifications()
                }
            }
        }
    }
}