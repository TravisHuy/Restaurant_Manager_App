package com.nhathuy.restaurant_manager_app.data.dto
/**
 * Data Transfer Object for Table creation
 *
 * @version 0.1
 * @since 12-02-2025
 * @author TravisHuy
 */
data class TableDto(
    /** Table number */
    val number: Int,
    /** Table capacity */
    val capacity: Int,
    /** Availability status of the table */
    val available: Boolean,
)
