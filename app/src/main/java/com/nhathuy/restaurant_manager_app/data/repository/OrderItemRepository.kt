package com.nhathuy.restaurant_manager_app.data.repository

import com.nhathuy.restaurant_manager_app.data.api.OrderItemService
import com.nhathuy.restaurant_manager_app.data.dto.OrderItemDTO
import com.nhathuy.restaurant_manager_app.data.model.OrderItem
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Repository for managing order-related operations.
 *
 * @since 0.1
 * @version 24-02-2025
 * @author TravisHuy
 *
 * @param orderItemService the service for order-related operations
 */
class OrderItemRepository @Inject constructor(private val orderItemService: OrderItemService) {

    suspend fun getOrderItemById(orderItemId:String) : Flow<Resource<OrderItemDTO>> = flow {
        emit(Resource.Loading())
        try {
            val response = orderItemService.getOrderItemById(orderItemId)
            if(response.isSuccessful){
                emit(Resource.Success(response.body()!!))
            }
            else{
                emit(Resource.Error("Failed to get order: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Error : ${e.message}"))
        }
    }
    suspend fun getListOrderItem(orderItemIds: List<String>) : Flow<Resource<List<OrderItemDTO>>> = flow {
        emit(Resource.Loading())
        try {
            val response = orderItemService.getListOrderItem(orderItemIds)
            if(response.isSuccessful){
                emit(Resource.Success(response.body()!!))
            }
            else{
                emit(Resource.Error("Failed to get list orders: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Error : ${e.message}"))
        }
    }
}