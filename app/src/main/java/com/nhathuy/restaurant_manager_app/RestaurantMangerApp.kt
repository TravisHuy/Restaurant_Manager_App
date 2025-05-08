package com.nhathuy.restaurant_manager_app

import android.app.Application
import android.content.Intent
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.di.component.DaggerRestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.component.RestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.module.RestaurantManagerModule
import com.nhathuy.restaurant_manager_app.service.BackgroundNotificationWorker
import com.nhathuy.restaurant_manager_app.service.WebSocketService
import javax.inject.Inject

class RestaurantMangerApp : Application() {
    private lateinit var appComponent: RestaurantMangerComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerRestaurantMangerComponent.builder().restaurantManagerModule(
            RestaurantManagerModule(this)
        ).build()

        //start websocket service
       startWebSocketService()
    }
    fun getRestaurantComponent():RestaurantMangerComponent{
        return appComponent
    }
    private fun startWebSocketService(){
        val serviceIntent = Intent(this,WebSocketService::class.java)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}