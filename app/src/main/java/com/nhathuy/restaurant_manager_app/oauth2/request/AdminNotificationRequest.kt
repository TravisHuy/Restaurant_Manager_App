package com.nhathuy.restaurant_manager_app.oauth2.request

import com.nhathuy.restaurant_manager_app.data.model.NotificationType

data class AdminNotificationRequest(
    val title:String,
    val message:String,
    val type:NotificationType,
    val relateId:String,
    val recipientRoles:List<String>
)