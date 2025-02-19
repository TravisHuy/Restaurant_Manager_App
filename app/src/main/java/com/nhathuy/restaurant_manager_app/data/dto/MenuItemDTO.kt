package com.nhathuy.restaurant_manager_app.data.dto

data class MenuItemDTO(
    val id:String,
    val name:String,
    val description:String,
    val price: Double,
    val imageId:String,
    val categoryId:String,
    val available:Boolean
)