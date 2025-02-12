package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import com.nhathuy.restaurant_manager_app.data.model.Table
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Service for Table API
 *
 * @version 0.1
 * @since 12-02-2025
 * @author TravisHuy
 */
interface TableService {

    /**
     * Get all tables
     *
     * @return List of tables
     */
    @GET("/api/tables/alls")
    suspend fun getAllTables(): List<Table>

    /**
     * Add a new table
     *
     * @param tableDto The table data
     * @return The added table
     */
    @POST("/api/tables/add")
    suspend fun addTable(tableDto: TableDto): Table
}