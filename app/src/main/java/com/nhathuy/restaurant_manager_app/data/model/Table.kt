package com.nhathuy.restaurant_manager_app.data.model

/**
 * Represents a table entity in the system.
 *
 * @version 0.1
 * @since 12-02-2025
 * @author TravisHuy
 */
data class Table(
    /** Unique identifier for the table */
    val id: String,
    /** Table number */
    var number: Int,
    /** Table capacity */
    var capacity: Int,
    /** Availability status of the table */
    var available: Boolean,
    /** List of order IDs associated with the table */
    var orderIds: List<String>,
    /** List of reservation IDs associated with the table */
    var reservationIds: List<String>,
    /** Floor ID where the table is located */
    val floorId: String
)
