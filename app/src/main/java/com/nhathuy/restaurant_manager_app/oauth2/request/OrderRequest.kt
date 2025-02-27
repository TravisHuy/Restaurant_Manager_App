package com.nhathuy.restaurant_manager_app.oauth2.request

data class OrderRequest(
    val customerName: String,
    val tableId: String,
    val items: List<OrderItemRequest>
)