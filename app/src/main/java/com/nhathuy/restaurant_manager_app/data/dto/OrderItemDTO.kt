package com.nhathuy.restaurant_manager_app.data.dto

/**
 * Data Transfer Object for order item.
 *
 *
 * @since 0.1
 * @version 26-02-2025
 * @author TravisHuy

 */
data class OrderItemDTO(
    val id: String,
    val menuItems: List<MenuDTO>,
    val totalPrice: Double,
    val note: String?
)
