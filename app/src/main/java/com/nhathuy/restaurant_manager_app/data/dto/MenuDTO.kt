package com.nhathuy.restaurant_manager_app.data.dto

/**
 * Data Transfer Object for menu item.
 *
 *
 * @since 0.1
 * @version 26-02-2025
 * @author TravisHuy

 */
data class MenuDTO(
    val menuItemId: String,
    val menuItemName: String,
    val quantity: Int,
    val price: Double
)