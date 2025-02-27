package com.nhathuy.restaurant_manager_app.oauth2.response

import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.Status
import java.time.LocalDateTime
/**
 * Represents a response to the order token request.
 * This class is ordered to map the response body to a Java object.
 *
 * @version 0.1
 * @since 26-02-2025
 * @author TravisHuy
 */
data class OrderResponse(
    val id: String,
    val customerName: String,
    val tableId: String,
    val items: List<OrderItemDTO>,
    val orderTime: String,
    val status: Status,
    val totalAmount: Double
)
