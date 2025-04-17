package com.nhathuy.restaurant_manager_app.data.model

/**
 * Represents an administrative notification in the restaurant management system.
 *
 * This data class encapsulates information about notifications sent to administrators,
 * such as system alerts, order status changes, payment confirmations, or other
 * important events that require administrative attention.
 *
 * @author TravisHuy
 * @version 0.1
 * @since 17/04/2025
 *
 */
data class AdminNotification(
    val id:String ,
    val title:String,
    val message:String,
    val notificationType: NotificationType,
    val relatedId : String,
    val timestamp:String
)
