package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.Category
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Service for Category API
 *
 * @version 0.1
 * @since 18-02-2025
 * @author TravisHuy
 */
interface CategoryService {

    @POST("api/category/add")
    suspend fun addCategory(@Body category: Category): Category

    @GET("api/category/all")
    suspend fun getAllCategories(): List<Category>

    @GET("api/category/{id}")
    suspend fun getCategoryById(@Path("id") id: String): Category
}