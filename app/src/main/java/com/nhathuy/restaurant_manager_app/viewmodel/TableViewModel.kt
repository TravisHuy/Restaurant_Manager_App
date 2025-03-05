package com.nhathuy.restaurant_manager_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import com.nhathuy.restaurant_manager_app.data.model.Table
import com.nhathuy.restaurant_manager_app.data.repository.TableRepository
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * TableViewModel for managing table-related operations.
 * This ViewModel handles operations such as getting all tables, adding new tables, and checking reservations.
 *
 * @version 0.2
 * @since 12-02-2025
 * @author TravisHuy
 */
class TableViewModel @Inject constructor(
    private val tableRepository: TableRepository
) : ViewModel() {

    // LiveData for all tables
    private val _tables = MutableLiveData<Resource<List<Table>>>()
    val tables: LiveData<Resource<List<Table>>> = _tables

    // LiveData for adding a table
    private val _addTableResult = MutableLiveData<Resource<Table>>()
    val addTableResult: LiveData<Resource<Table>> = _addTableResult

    // LiveData for table reservation status
    private val _checkTableReservationResult = MutableLiveData<Resource<Boolean>>()
    val checkTableReservationResult: LiveData<Resource<Boolean>> = _checkTableReservationResult

    // LiveData for tables by floor
    private val _tablesByFloor = MutableLiveData<Resource<List<Table>>>()
    val tablesByFloor: LiveData<Resource<List<Table>>> = _tablesByFloor

    /**
     * Fetches all tables from the repository.
     */
    fun getAllTables() {
        viewModelScope.launch {
            _tables.value = Resource.Loading()
            _tables.value = tableRepository.getAllTables()
        }
    }

    /**
     * Adds a new table to the system
     *
     * @param tableDto The data transfer object for the table
     * @param floorId The ID of the floor where the table will be added
     */
    fun addTable(tableDto: TableDto, floorId: String) {
        viewModelScope.launch {
            _addTableResult.value = Resource.Loading()
            val result = tableRepository.addTable(tableDto, floorId)
            _addTableResult.value = result

            // If table is added successfully, refresh the tables list
            if (result is Resource.Success) {
                getAllTables()
            }
        }
    }

    /**
     * Checks the reservation status of a specific table
     *
     * @param tableId The ID of the table to check
     */
    fun checkTableReservation(tableId: String) {
        viewModelScope.launch {
            _checkTableReservationResult.value = Resource.Loading()
            _checkTableReservationResult.value = tableRepository.checkTableReservation(tableId)
        }
    }

    /**
     * Fetches tables for a specific floor
     *
     * @param floorId The ID of the floor
     */
    fun getTablesByFloorId(floorId: String) {
        viewModelScope.launch {
            _tablesByFloor.value = Resource.Loading()
            _tablesByFloor.value = tableRepository.getTablesByFloorId(floorId)
        }
    }
}