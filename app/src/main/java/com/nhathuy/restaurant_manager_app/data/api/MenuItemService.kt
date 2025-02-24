package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.dto.MenuItemDTO
import com.nhathuy.restaurant_manager_app.data.model.MenuItem
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

/**
 * Service for managing menu item-related operations.
 *
 * @return 0.1
 * @since 20-02-2025
 * @author TravisHuy
 */
interface MenuItemService {

    @Multipart
    @POST("api/menu-items/add/{categoryId}")
    suspend fun createMenuItem(
        @Part("menuItem") menuItemDTO: RequestBody,
        @Part image: MultipartBody.Part?,
        @Path("categoryId") categoryId: String
    ): Response<MenuItem>

    @GET("api/menu-items/all")
    suspend fun getAllMenuItems(): Response<List<MenuItem>>

    @GET("api/menu-items/{id}/image")
    suspend fun getMenuItemImage(@Path("id") id: String): Response<ResponseBody>

    @PUT("api/menu-items/addNote/{id}")
    suspend fun addNoteToMenuItem(@Path("id") id: String, @Part("note") note: RequestBody): Response<MenuItem>

}