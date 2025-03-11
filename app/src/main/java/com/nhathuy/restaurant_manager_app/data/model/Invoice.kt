package com.nhathuy.restaurant_manager_app.data.model

import java.time.LocalDateTime
/**
 * Represents a invoice entity in the system.
 *
 * @version 0.1
 * @since 24-02-2025
 * @author TravisHuy
 */
data class Invoice(
    val id:String ,
    val orderId:String,
    val totalAmount:Double,
    val paymentTime: String,
    val paymentMethod:PaymentMethod,
)
