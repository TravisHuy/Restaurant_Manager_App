package com.nhathuy.restaurant_manager_app.data.model

import java.time.LocalDateTime
/**
 * Represents a order entity in the system.
 *
 * @version 0.1
 * @since 24-02-2025
 * @author TravisHuy
 */
data class Order(
    val id:String,
    val customerName:String,
    val tableId:String,
    val invoiceId:String,
    val orderItemIds:List<String>,
    val orderTime: String,
    val status:Status,
    val totalAmount:Double
)
