package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.model.Floor
import com.nhathuy.restaurant_manager_app.data.repository.FloorRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * FloorViewModel for managing floor-related operations.
 * This ViewModel handles operations such as getting all floors and adding new floors.
 *
 * @version 0.1
 * @since 14-02-2025
 * @author TravisHuy
 */
class FloorViewModel @Inject constructor(private val floorRepository: FloorRepository):ViewModel(){
    private val _floors = MutableLiveData<Resource<List<Floor>>>()
    val floors: LiveData<Resource<List<Floor>>> = _floors

    private val _addFloorResult = MutableLiveData<Resource<Floor>>()
    val addFloorResult: LiveData<Resource<Floor>> = _addFloorResult

    /**
     * Fetches all floors from the repository.
     * Updates the floors LiveData with the result.
     */
    fun getAllFloors() {
        viewModelScope.launch {
            _floors.value = Resource.Loading()
            try {
                val response = floorRepository.getAllFloors()
                if (response.isNotEmpty()) {
                    _floors.value = Resource.Success(response)
                } else {
                    _floors.value = Resource.Error("No floors found")
                }
            } catch (e: Exception) {
                _floors.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
    /**
     * Adds a new floor to the system
     * Updates the addFloorResult LiveData with the result.
     *
     * @param floor The floor to be added
     */
    fun addFloor(floor: Floor){
        viewModelScope.launch {
            _addFloorResult.value = Resource.Loading()
            try {
                val response = floorRepository.addFloor(floor)
                _addFloorResult.value = Resource.Success(response)
            }
            catch (e:Exception){
                _addFloorResult.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    fun getFloorById(floorId:String) {
        viewModelScope.launch {
            _floors.value = Resource.Loading()
            try {
                val response = floorRepository.getFloorById(floorId)
                if (response != null) {
                    _floors.value = Resource.Success(listOf(response))
                } else {
                    _floors.value = Resource.Error("No floor found")
                }
            } catch (e: Exception) {
                _floors.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
}