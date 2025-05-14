//package com.nhathuy.restaurant_manager_app.di.module
//
//import android.content.Context
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder
//import com.nhathuy.restaurant_manager_app.data.api.NotificationRetrofit
//import com.nhathuy.restaurant_manager_app.data.api.NotificationService
//import com.nhathuy.restaurant_manager_app.data.api.PaymentService
//import com.nhathuy.restaurant_manager_app.data.local.TokenManager
//import com.nhathuy.restaurant_manager_app.service.NotificationAdminService
//import com.nhathuy.restaurant_manager_app.service.RestaurantWebSocketClient
//import com.nhathuy.restaurant_manager_app.util.Constants
//import dagger.Module
//import dagger.Provides
//import okhttp3.OkHttpClient
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import javax.inject.Singleton
//
///**
// * Dagger module for providing notification-related dependencies
// *
// * @version 0.1
// * @since 08-05-2025
// * @author TravisHuy
// */
//@Module
//class NotificationWebsocketModule {
//    /**
//     * Provides Gson instance for JSON parsing
//     */
//    @Singleton
//    @Provides
//    fun provideGson(): Gson {
//        return GsonBuilder()
//            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//            .create()
//    }
//
//
//    /**
//     * Provides a singleton instance of Retrofit for admin notification related requests.
//     *
//     * @return A configured [Retrofit] instance for handling order operations.
//     */
//    @NotificationRetrofit
//    @Provides
//    @Singleton
//    fun provideNotificationRetrofit(okHttpClient: OkHttpClient,gson: Gson) : Retrofit {
//        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create(gson))
//            .build();
//    }
//
//    /**
//     * Provides a singleton instance of [PaymentService] for handling payemnt related requests.
//     *
//     * @param retrofit The [Retrofit] instance used to create the service.
//     * @return An implementation of [PaymentService].
//     */
//    @Provides
//    @Singleton
//    fun provideNotificationApi(@NotificationRetrofit retrofit: Retrofit): NotificationService {
//        return retrofit.create(NotificationService::class.java);
//    }
//
//    /**
//     * Provides WebSocketClient for real-time notifications
//     */
//    @Singleton
//    @Provides
//    fun provideWebSocketClient(tokenManager: TokenManager): RestaurantWebSocketClient {
//        return RestaurantWebSocketClient(tokenManager)
//    }
//
//
//    /**
//     * Provides NotificationService for real-time notifications
//     */
//    @Singleton
//    @Provides
//    fun provideNotificationService(context:Context): NotificationAdminService {
//        return NotificationAdminService(context)
//    }
//
//
//}