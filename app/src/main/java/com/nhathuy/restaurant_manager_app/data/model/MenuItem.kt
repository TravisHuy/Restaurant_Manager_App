package com.nhathuy.restaurant_manager_app.data.model

/**
 * Represents a menuItem entity in the system.
 *
 * @version 0.1
 * @since 18-02-2025
 * @author TravisHuy
 */
data class MenuItem(
    /**
     * The unique identifier of the menuItem
     */
    val id:String,
    /**
     * The name of the menuItem
     */
    val name:String,
    /**
     * The description of the menuItem
     */
    val description:String,
    /**
     * The price of the menuItem
     */
    val price: Double,
    /**
     * The imageId of the menuItem
     */
    val imageData:String,
    /**
     * The categoryId of the menuItem
     */
    val categoryId:String,
    /**
     * The availability of the menuItem
     */
    val available:Boolean,
    /**
     * Most note
     */
    val note:String
)
