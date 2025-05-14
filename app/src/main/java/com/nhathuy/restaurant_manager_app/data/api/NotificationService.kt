package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.data.model.FcmTokenRequest
import com.nhathuy.restaurant_manager_app.data.model.Notification
import com.nhathuy.restaurant_manager_app.oauth2.request.AdminNotificationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface NotificationService {

//    @GET("api/notifications/all")
//    suspend fun getAllNotification(): Response<List<AdminNotification>>
//
//    @GET("api/notifications/unread")
//    suspend fun getUnreadNotifications(): Response<List<AdminNotification>>
//
//    @PUT("api/notifications/{id}/read")
//    suspend fun markAsRead(@Path("id") id:String): Response<Void>

    @POST("api/user/fcm-token")
    suspend fun updateFcmToken(@Body request:FcmTokenRequest):Response<Void>

    @GET("api/notifications/admin")
    suspend fun getAdminNotification() : Response<List<Notification>>

    @GET("api/notifications/manager")
    suspend fun getManagerNotification() : Response<List<Notification>>

    @GET("api/notifications/employee")
    suspend fun getEmployeeNotification() : Response<List<Notification>>

    @GET("api/notifications/unread/{role}")
    suspend fun getUnreadNotification(@Path("role") role:String):Response<List<Notification>>

    @PUT("api/notifications/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId:String):Response<List<Notification>>

    @PUT("api/notification/read-all/{role}")
    suspend fun markAllAsRead(@Path("role") role:String) :Response<Void>

    @GET("api/notification/entity/{entityId}")
    suspend fun getNotificationForEntity(@Path("entity") entityId:String)  :Response<List<Notification>>

    @POST("api/notifications/admin/create")
    suspend fun createAdminNotification(@Body adminNotificationRequest: AdminNotificationRequest) : Response<Notification>
}