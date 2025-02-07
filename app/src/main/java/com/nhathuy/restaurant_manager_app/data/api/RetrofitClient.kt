package com.nhathuy.restaurant_manager_app.data.api

import com.nhathuy.restaurant_manager_app.util.Constants
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
object RetrofitClient {

    @Provides
    @Singleton
    fun provideRetrofit() : Retrofit{
        return Retrofit.Builder().baseUrl(Constants.AUTH_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthService{
        return retrofit.create(AuthService::class.java);
    }
}