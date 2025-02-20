package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.dto.MenuItemDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface MenuItemService {

    @Multipart
    @POST("api/menu-items/add/{categoryId}")
    suspend fun addMenuItem(@Part("name") name: RequestBody,
                            @Part("description") description: RequestBody,
                            @Part("price") price: RequestBody,
                            @Part("available") available: RequestBody,
                            @Part image: MultipartBody.Part?,
                            @Path("categoryId") categoryId: String
    ): Response<ResponseBody>

    @GET("api/menu-items/{id}/image")
    suspend fun getMenuItemImage(@Path("id") id: String): Response<ResponseBody>
}