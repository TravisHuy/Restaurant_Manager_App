package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.Category
import retrofit2.http.Body
import retrofit2.http.POST

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
}