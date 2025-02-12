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
     * Provides a singleton instance of Retrofit for authentication-related requests.
     *
     * @return A configured [Retrofit] instance for authentication.
     */
    @AuthRetrofit
    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }
    /**
     * Provides a singleton instance of [AuthService] for handling authentication requests.
     *
     * @param retrofit The [Retrofit] instance used to create the service.
     * @return An implementation of [AuthService].
     */
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthService{
        return retrofit.create(AuthService::class.java);
    }

    /**
     * Provides a singleton instance of Retrofit for table-related requests.
     *
     * @return A configured [Retrofit] instance for handling table operations.
     */
    @TableRetrofit
    @Provides
    @Singleton
    fun provideTableRetrofit() : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    /**
     * Provides a singleton instance of [TableService] for handling table-related requests.
     *
     * @param retrofit The [Retrofit] instance used to create the service.
     * @return An implementation of [TableService].
     */
    @Provides
    @Singleton
    fun provideTableApi(retrofit: Retrofit): TableService{
        return retrofit.create(TableService::class.java);
    }
}