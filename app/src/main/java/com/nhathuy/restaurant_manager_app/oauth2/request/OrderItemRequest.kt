package com.nhathuy.restaurant_manager_app.oauth2.request

data class OrderItemRequest(
    val menuItems: List<MenuItemRequest>,
    val note:String
)