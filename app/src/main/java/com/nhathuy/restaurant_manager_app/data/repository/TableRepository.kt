package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.TableService
import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import javax.inject.Inject

/**
 * The repository class for the table service.
 * This class is used to interact with the table service to perform add, get all tables, and other table operations.
 *
 * @version 0.1
 * @since 05-02-2025
 * @author TravisHuy
 */
class TableRepository @Inject constructor(private val tableService: TableService){
    suspend fun getAllTables() = tableService.getAllTables()
    suspend fun addTable(tableDto: TableDto) = tableService.addTable(tableDto)
}