package com.nhathuy.restaurant_manager_app.di.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton
/**
 * This class provides the RestaurantManagerModule module for the application.
 * The RestaurantManagerModule module is used to provide the context for the application.
 *
 * @version 0.1
 * @since 07-02-2025
 * @author TravisHuy
 */
@Module
class RestaurantManagerModule(private val application: Application) {

    /**
     * Provides the context for the application.
     *
     * @return The context for the application
     */
    @Provides
    @Singleton
    fun provideContext() : Context{
        return application.applicationContext
    }
}