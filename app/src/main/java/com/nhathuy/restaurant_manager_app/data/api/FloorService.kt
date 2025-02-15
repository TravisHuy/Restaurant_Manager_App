package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.Floor
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Service for Floor API
 *
 * @version 0.1
 * @since 12-02-2025
 * @author TravisHuy
 */
interface FloorService {

    @GET("api/floors/all")
    suspend fun getAllFloors(): List<Floor>

    @GET("api/floors/{id}")
    suspend fun getFloorById(@Path("id") id: String): Floor

    @POST("api/floors/add")
    suspend fun addFloor(@Body floor: Floor): Floor
}