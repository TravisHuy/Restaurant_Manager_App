package com.nhathuy.restaurant_manager_app

import android.app.Application
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.di.component.DaggerRestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.component.RestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.module.RestaurantManagerModule
import com.nhathuy.restaurant_manager_app.service.BackgroundNotificationWorker
import javax.inject.Inject

class RestaurantMangerApp : Application() {
    private lateinit var appComponent: RestaurantMangerComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerRestaurantMangerComponent.builder().restaurantManagerModule(
            RestaurantManagerModule(this)
        ).build()

        val tokenManager = TokenManager(this)
        val isLoggedIn = tokenManager.isUserLoggedIn()
        val isAdmin = tokenManager.getUserRole() == "ROLE_ADMIN"

        if(isLoggedIn && isAdmin){
            BackgroundNotificationWorker.sheduleWork(this)
        }
    }
    fun getRestaurantComponent():RestaurantMangerComponent{
        return appComponent
    }
}