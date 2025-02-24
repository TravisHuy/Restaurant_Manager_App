package com.nhathuy.restaurant_manager_app.data.dto

data class MenuItemDTO(
    val name:String,
    val description:String,
    val price: Double,
    val imageData:String,
    val categoryId:String,
    val available:Boolean=true,
    val note:String
)