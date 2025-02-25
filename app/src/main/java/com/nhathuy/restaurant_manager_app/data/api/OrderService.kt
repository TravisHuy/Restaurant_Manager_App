package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.Order
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
/**
 * The service interface for the order service.
 * This interface is used to interact with the order service to perform add, remove, and confirm order operations.
 *
 * @version 0.1
 * @since 24-02-2025
 * @author TravisHuy
 */
interface OrderService {

    @POST("api/orders/add-item")
    suspend fun addOrder(@Query("tableId") tableId:String ,
                         @Query("menuItemId") menuItemId:String,
                         @Query("quantity") quantity:Int,
                         @Query("price") price:Double
    ):Response<String>

    @DELETE("api/orders/remove-item")
    suspend fun removeItem(@Query("tableId") tableId:String ,
                           @Query("menuItemId") menuItemId:String
    ):Response<String>

    @POST("api/orders/confirm")
    suspend fun confirmOrder(@Query("tableId") tableId:String,@Query("customerName") customerName:String):Response<Order>

    @GET("api/orders/temp-items")
    suspend fun getTempItems(@Query("tableId") tableId:String):Response<List<Order>>
}