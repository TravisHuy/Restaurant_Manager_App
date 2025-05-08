package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.data.repository.AdminNotificationRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class AdminNotificationViewModel @Inject constructor(private val adminNotificationRepository: AdminNotificationRepository) : ViewModel() {

    private val _notificationState = MutableLiveData<Resource<List<AdminNotification>>>()
    val notificationState : LiveData<Resource<List<AdminNotification>>> =_notificationState

    private val _unreadNotificationsState = MutableLiveData<Resource<List<AdminNotification>>>()
    val unreadNotificationsState : LiveData<Resource<List<AdminNotification>>> =_unreadNotificationsState

    private val _markAsReadState = MutableLiveData<Resource<Unit>>()
    val markAsReadState: LiveData<Resource<Unit>> = _markAsReadState

    fun loadAllNotifications(){
        viewModelScope.launch {
            adminNotificationRepository.loadNotifications().collectLatest {
                result ->
                _notificationState.value = result
            }
        }
    }

    fun loadUnreadNotifications(){
        viewModelScope.launch {
            adminNotificationRepository.getUnreadNotifications().collectLatest {
                    result ->
                _unreadNotificationsState.value = result
            }
        }
    }

    fun markNotificationAsRead(id: String) {
        viewModelScope.launch {
            adminNotificationRepository.markAsRead(id).collectLatest { result ->
                _markAsReadState.value = result

                if (result is Resource.Success) {
                    loadAllNotifications()
                    loadUnreadNotifications()
                }
            }
        }
    }

    init {
        loadAllNotifications()
        loadUnreadNotifications()
    }
}