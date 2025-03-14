package com.nhathuy.restaurant_manager_app.data.repository

import android.util.Log
import com.nhathuy.restaurant_manager_app.data.api.OrderService
import com.nhathuy.restaurant_manager_app.data.model.Order
import com.nhathuy.restaurant_manager_app.oauth2.request.OrderItemRequest
import com.nhathuy.restaurant_manager_app.oauth2.request.OrderRequest
import com.nhathuy.restaurant_manager_app.oauth2.response.OrderResponse
import com.nhathuy.restaurant_manager_app.resource.Resource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

/**
 * Repository for managing order-related operations.
 *
 * @since 0.1
 * @version 24-02-2025
 * @author TravisHuy
 *
 * @param orderService the service for order-related operations
 */
class OrderRepository @Inject constructor(private val orderService: OrderService) {

    suspend fun addOrder(tableId:String , menuItemId:String, quantity:Int, price:Double) : Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = orderService.addOrder(tableId, menuItemId, quantity, price)
            if(response.isSuccessful) {
                emit(Resource.Success(response.body()?:"Item added successfully"))
            }
            else{
                emit(Resource.Error("Failed to add item: ${response.message()}"))

            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }

    suspend fun getAllOrders() : Flow<Resource<List<Order>> > = flow {
        emit(Resource.Loading())
        try {
            val response = orderService.getAllOrders()
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to get all orders: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
    suspend fun getOrderById(orderId:String) : Flow<Resource<Order>> = flow {
        emit(Resource.Loading())
        try {
            val response = orderService.getOrderById(orderId)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to get order by id: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }

    suspend fun updateOrderStatus(orderId: String): Flow<Resource<OrderResponse>> = channelFlow {
        send(Resource.Loading())
        try {
            val response = withContext(Dispatchers.IO) {
                orderService.updateOrderStatus(orderId)
            }

            when {
                response.isSuccessful -> {
                    response.body()?.let {
                        send(Resource.Success(it))
                    } ?: send(Resource.Error("Empty response body from server"))
                }
                else -> {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.takeIf { it.isNotEmpty() }
                        ?: "Failed to update status order by id: ${response.message()}"
                    send(Resource.Error(errorMessage))
                }
            }
        } catch (e: Exception) {
            when (e) {
                is CancellationException -> throw e
                else -> {
                    Log.e("OrderRepository", "Update order status failed", e)
                    send(Resource.Error("Network error: ${e.message}"))
                }
            }
        }
    }.flowOn(Dispatchers.IO)

    suspend fun removeItem(tableId:String , menuItemId:String) : Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response  = orderService.removeItem(tableId, menuItemId)
            if(response.isSuccessful){
                emit(Resource.Success(response.body()?:"Item removed successfully"))
            }
            else{
                emit(Resource.Error("Failed to remove item: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }

    suspend fun confirmOrder(tableId:String , customerName:String) : Flow<Resource<Order>>  = flow {
        emit(Resource.Loading())
        try {
            val response = orderService.confirmOrder(tableId, customerName)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to confirm order: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
    suspend fun getTempItems(tableId:String) : Flow<Resource<List<Order>>> = flow{
        emit(Resource.Loading())
        try {
            val response = orderService.getTempItems(tableId)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to get temp items: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))

        }
    }

    suspend fun createOrder(orderRequest: OrderRequest) : Flow<Resource<OrderResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = orderService.createOrder(orderRequest)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to create order: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
            Log.d("OrderRepository", "error: ${e.message}")
        }
    }
    suspend fun getCustomerName(tableId:String) : Flow<Resource<String>> = flow {
        emit(Resource.Loading())
        try {
            val response = orderService.getCustomerName(tableId)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to get customer name: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }

    suspend fun addItemToOrder(orderId:String, newItems:List<OrderItemRequest>) : Flow<Resource<OrderResponse>> = flow {
        emit(Resource.Loading())
        try {
            val response = orderService.addItemToOrder(orderId, newItems)
            if(response.isSuccessful){
                response.body()?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("empty response body server"))
            }
            else{
                emit(Resource.Error("Failed to add items to order: ${response.message()}"))
            }
        }
        catch (e:Exception){
            emit(Resource.Error("Network error: ${e.message}"))
        }
    }
}