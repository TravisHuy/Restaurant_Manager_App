package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.TableService
import com.nhathuy.restaurant_manager_app.data.dto.TableDto
import com.nhathuy.restaurant_manager_app.data.model.Table
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

/**
 * The repository class for the table service.
 * This class is used to interact with the table service to perform add, get all tables, and other table operations.
 *
 * @version 0.2
 * @since 05-02-2025
 * @author TravisHuy
 */
class TableRepository @Inject constructor(private val tableService: TableService) {

    /**
     * Fetches all tables with proper error handling
     *
     * @return Resource of List of Tables
     */
    suspend fun getAllTables(): Resource<List<Table>> = withContext(Dispatchers.IO) {
        try {
            val response = tableService.getAllTables()
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Failed to fetch tables: ${response.message()}")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown HTTP error"
            Resource.Error("HTTP Error: $errorBody")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Adds a new table to the system
     *
     * @param tableDto The data transfer object for the table
     * @param floorId The ID of the floor where the table will be added
     * @return Resource of the added Table
     */
    suspend fun addTable(tableDto: TableDto, floorId: String): Resource<Table> = withContext(Dispatchers.IO) {
        try {
            val addedTable = tableService.addTable(tableDto, floorId)
            Resource.Success(addedTable)
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown HTTP error"
            Resource.Error("HTTP Error: $errorBody")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Checks table reservation status
     *
     * @param tableId The ID of the table to check
     * @return Resource of Boolean indicating reservation status
     */
    suspend fun checkTableReservation(tableId: String): Resource<Boolean> = withContext(Dispatchers.IO) {
        try {
            val response = tableService.checkTableReservation(tableId)
            if (response.isSuccessful) {
                Resource.Success(response.body() ?: false)
            } else {
                Resource.Error("Failed to check table reservation: ${response.message()}")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown HTTP error"
            Resource.Error("HTTP Error: $errorBody")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message ?: "Unknown error"}")
        }
    }

    /**
     * Fetches tables by floor ID
     *
     * @param floorId The ID of the floor
     * @return Resource of List of Tables
     */
    suspend fun getTablesByFloorId(floorId: String): Resource<List<Table>> = withContext(Dispatchers.IO) {
        try {
            val response = tableService.getTablesByFloorId(floorId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Empty response")
            } else {
                Resource.Error("Failed to fetch tables by floor: ${response.message()}")
            }
        } catch (e: HttpException) {
            val errorBody = e.response()?.errorBody()?.string() ?: "Unknown HTTP error"
            Resource.Error("HTTP Error: $errorBody")
        } catch (e: Exception) {
            Resource.Error("Error: ${e.message ?: "Unknown error"}")
        }
    }

    suspend fun getTablesByOrderId(orderId:String) : Flow<Resource<Table>> = flow {
        emit(Resource.Loading())
        try {
            val response = tableService.getTableByOrderId(orderId)
            if(response.isSuccessful){
                response?.body()?.let {
                    emit(Resource.Success(it))
                }?:emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to get order by id: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
}