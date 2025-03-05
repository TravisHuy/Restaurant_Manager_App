package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import com.nhathuy.restaurant_manager_app.data.model.Table
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
    @GET("api/tables/alls")
    suspend fun getAllTables(): Response<List<Table>>

    /**
     * Add a new table
     *
     * @param tableDto The table data
     * @return The added table
     */
    @POST("api/tables/add/{floorId}")
    suspend fun addTable(@Body tableDto: TableDto, @Path("floorId") floorId:String): Table

    /**
     * Check if a table is reserved
     *
     * @param tableId The table ID
     * @return True if the table is reserved, false otherwise
     */
    @GET("api/tables/check/{tableId}")
    suspend fun checkTableReservation(@Path("tableId") tableId: String):Response<Boolean>

    /**
     * Get all tables by floor ID
     *
     * @param floorId The floor ID
     * @return List of tables
     */
    @GET("api/tables/byFloor/{floorId}")
    suspend fun getTablesByFloorId(@Path("floorId") floorId: String): Response<List<Table>>
}