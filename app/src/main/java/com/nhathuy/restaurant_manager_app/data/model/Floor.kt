package com.nhathuy.restaurant_manager_app.data.model

/**
 * Represents a floor entity in the system.
 *
 * @version 0.1
 * @since 14-02-2025
 * @author TravisHuy
 */
data class Floor(
    val id: String,
    val name: String,
    val tableIds: List<String>
){
    constructor(name:String):this("",name, emptyList())
}