package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.FloorService
import com.nhathuy.restaurant_manager_app.data.model.Floor
import javax.inject.Inject

/**
 * The repository class for the floor service.
 * This class is used to interact with the floor service to perform add, get all floors, and other floor operations.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
class FloorRepository @Inject constructor(private val floorService:FloorService){
    suspend fun getAllFloors() = floorService.getAllFloors()
    suspend fun addFloor(floor: Floor) = floorService.addFloor(floor)
    suspend fun getFloorById(id:String) = floorService.getFloorById(id)
}