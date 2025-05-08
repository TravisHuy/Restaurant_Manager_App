package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationService {

    @GET("api/notifications/all")
    suspend fun getAllNotification(): Response<List<AdminNotification>>

    @GET("api/notifications/unread")
    suspend fun getUnreadNotifications(): Response<List<AdminNotification>>

    @PUT("api/notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id:String): Response<Void>
}