package com.nhathuy.restaurant_manager_app.di.module

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.nhathuy.restaurant_manager_app.data.api.NotificationRetrofit
import com.nhathuy.restaurant_manager_app.data.api.NotificationService
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.model.FcmTokenRequest
import com.nhathuy.restaurant_manager_app.data.repository.FcmTokenRepository
import com.nhathuy.restaurant_manager_app.data.repository.NotificationRepository
import com.nhathuy.restaurant_manager_app.util.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
class NotificationFirebaseModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()
    }

    /**
     * Provides a singleton instance of Retrofit for admin notification related requests.
     *
     * @return A configured [Retrofit] instance for handling order operations.
     */
    @NotificationRetrofit
    @Provides
    @Singleton
    fun provideNotificationRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    }

    /**
     * Provides a singleton instance of [NotificationService] for handling notification related requests.
     *
     * @param retrofit The [Retrofit] instance used to create the service.
     * @return An implementation of [NotificationService].
     */
    @Provides
    @Singleton
    fun provideNotificationApi(@NotificationRetrofit retrofit: Retrofit): NotificationService {
        return retrofit.create(NotificationService::class.java);
    }

    @Singleton
    @Provides
    fun provideFCMTokenRepository(
        notificationService: NotificationService,
        sessionManager: SessionManager
    ): FcmTokenRepository {
        return FcmTokenRepository(notificationService, sessionManager)
    }

    @Singleton
    @Provides
    fun provideNotificationRepository(
        notificationService: NotificationService,
        sessionManager: SessionManager
    ): NotificationRepository {
        return NotificationRepository(notificationService, sessionManager)
    }

}