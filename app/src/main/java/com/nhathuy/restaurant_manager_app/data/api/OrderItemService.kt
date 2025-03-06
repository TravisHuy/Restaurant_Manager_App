package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.OrderItem
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
/**
 * Service for managing order item-related operations.
 *
 * @return 0.1
 * @since 06-03-2025
 * @author TravisHuy
 */
interface OrderItemService {

    @GET("api/orderItems/getOrderItemById/{orderItemId}")
    suspend fun getOrderItemById(@Path("orderItemId") orderItemId:String) : Response<OrderItem>

    @GET("api/orderItems/getOrderItemByIds/{orderItemIds}")
    suspend fun getListOrderItem(@Path("orderItemIds") orderItemIds:List<String>) : Response<List<OrderItem>>
}