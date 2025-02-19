package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.util.Constants
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
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
    fun provideRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .client(okHttpClient)
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
    fun provideAuthApi(@AuthRetrofit retrofit: Retrofit): AuthService{
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
    fun provideTableRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .client(okHttpClient)
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
    fun provideTableApi(@TableRetrofit retrofit: Retrofit): TableService{
        return retrofit.create(TableService::class.java);
    }

    /**
     * Provides OKHttpClient with auth interceptor.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(tokenManager: TokenManager): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor {
                chain ->
                val original = chain.request()
                val token = tokenManager.getAccessToken()

                val request = if(token != null){
                    original.newBuilder()
                        .header("Authorization","Bearer $token")
                        .build()
                }
                else {
                    original
                }
                chain.proceed(request)
            }.build()
    }


    /**
     * Provides a singleton instance of Retrofit for floor-related requests.
     *
     * @return A configured [Retrofit] instance for handling floor operations.
     */
    @FloorRetrofit
    @Provides
    @Singleton
    fun provideFloorRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    /**
     * Provides a singleton instance of [FloorService] for handling floor-related requests.
     *
     * @param retrofit The [Retrofit] instance used to create the service.
     * @return An implementation of [FloorService].
     */
    @Provides
    @Singleton
    fun provideFloorApi(@FloorRetrofit retrofit: Retrofit): FloorService{
        return retrofit.create(FloorService::class.java);
    }

    /**
     * Provides a singleton instance of Retrofit for category-related requests.
     *
     * @return A configured [Retrofit] instance for handling floor operations.
     */
    @CategoryRetrofit
    @Provides
    @Singleton
    fun provideCategoryRetrofit(okHttpClient: OkHttpClient) : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    /**
     * Provides a singleton instance of [CategoryService] for handling category-related requests.
     *
     * @param retrofit The [Retrofit] instance used to create the service.
     * @return An implementation of [CategoryService].
     */
    @Provides
    @Singleton
    fun provideCategoryApi(@CategoryRetrofit retrofit: Retrofit): CategoryService{
        return retrofit.create(CategoryService::class.java);
    }
}