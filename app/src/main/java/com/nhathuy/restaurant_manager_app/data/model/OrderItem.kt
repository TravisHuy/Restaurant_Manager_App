package com.nhathuy.restaurant_manager_app.data.model
/**
 * Represents a order item entity in the system.
 *
 * @version 0.1
 * @since 14-02-2025
 * @author TravisHuy
 */
data class OrderItem(
    val id:String,
    val menuItemIds: List<OrderItemDetail>,
    val quantity: Int,
    val price:Double
){
    data class OrderItemDetail(
        val menuItemId:String,
        val menuItemName:String,
        val quantity: Int,
        val price:Double
    )
}
