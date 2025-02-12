package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.util.Constants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
/**
 * This class provides the Retrofit client for the application.
 * The Retrofit client is used to make network requests to the server.
 *
 * @version 0.1
 * @since 07-02-2025
 * @author TravisHuy
 */
@Module
object RetrofitClient {

    /**
     * Provides the Retrofit client for the application.
     *
     * @return The Retrofit client
     */
    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
    /**
     * Provides the authentication API service for the application.
     *
     * @param retrofit The Retrofit client
     * @return The authentication API service
     */
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthService{
        return retrofit.create(AuthService::class.java);
    }
}