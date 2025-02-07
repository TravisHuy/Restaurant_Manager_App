package com.nhathuy.restaurant_manager_app.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RestaurantManagerModule(private val application: Application) {

    @Provides
    @Singleton
    fun provideContext() : Context{
        return application.applicationContext
    }
}