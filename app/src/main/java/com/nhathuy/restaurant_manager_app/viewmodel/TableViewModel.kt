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
 * This ViewModel handles operations such as getting all tables and adding new tables.
 *
 * @version 0.1
 * @since 12-02-2025
 * @author TravisHuy
 */
class TableViewModel @Inject constructor(private val tableRepository: TableRepository): ViewModel() {

    private val _tables = MutableLiveData<Resource<List<Table>>>()
    val tables: LiveData<Resource<List<Table>>> = _tables

    private val _addTableResult = MutableLiveData<Resource<Table>>()
    val addTableResult: LiveData<Resource<Table>> = _addTableResult

    /**
     * Fetches all tables from the repository.
     * Updates the tables LiveData with the result.
     */
    fun getAllTables() {
        viewModelScope.launch {
            _tables.value = Resource.Loading()
            try {
                val response = tableRepository.getAllTables()
                if (response.isNotEmpty()) {
                    _tables.value = Resource.Success(response)
                } else {
                    _tables.value = Resource.Error("No tables found")
                }
            } catch (e: Exception) {
                _tables.value = Resource.Error("Error: ${e.message}")
            }
        }
    }

    /**
     * Adds a new table to the system
     * Updates the addTableResult LiveData with the result.
     *
     * @param tableDto The data transfer object for the table
     */
    fun addTable(tableDto:TableDto,floorId:String){
        viewModelScope.launch {
            _addTableResult.value = Resource.Loading()
            try {
                val response = tableRepository.addTable(tableDto, floorId)
                _addTableResult.value = Resource.Success(response)
                // refresh the tables list after successfully addtion
                getAllTables()
            } catch (e: Exception) {
                _addTableResult.value = Resource.Error("Error: ${e.message}")
            }
        }
    }
}