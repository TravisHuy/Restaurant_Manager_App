package com.nhathuy.restaurant_manager_app.data.model

import java.time.LocalDateTime

data class Notification(
    val id:String,
    val title:String,
    val message:String,
    val type:NotificationType,
    val relateId:String,
    val read:String,
    val timestamp: LocalDateTime,
    val recipientRoles: List<String>
)
