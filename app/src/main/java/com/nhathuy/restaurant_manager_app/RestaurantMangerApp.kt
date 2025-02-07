package com.nhathuy.restaurant_manager_app

import android.app.Application
import com.nhathuy.restaurant_manager_app.di.component.DaggerRestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.component.RestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.module.RestaurantManagerModule

class RestaurantMangerApp : Application() {
    private lateinit var appComponent: RestaurantMangerComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerRestaurantMangerComponent.builder().restaurantManagerModule(
            RestaurantManagerModule(this)
        ).build()
    }
    fun getRestaurantComponent():RestaurantMangerComponent{
        return appComponent
    }
}