package com.nhathuy.restaurant_manager_app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.di.component.DaggerRestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.component.RestaurantMangerComponent
import com.nhathuy.restaurant_manager_app.di.module.RestaurantManagerModule
import com.nhathuy.restaurant_manager_app.util.NotificationUtils.CHANNEL_DESCRIPTION
import com.nhathuy.restaurant_manager_app.util.NotificationUtils.CHANNEL_ID
import com.nhathuy.restaurant_manager_app.util.NotificationUtils.CHANNEL_NAME
import javax.inject.Inject

class RestaurantMangerApp : Application() {
    private lateinit var appComponent: RestaurantMangerComponent
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerRestaurantMangerComponent.builder().restaurantManagerModule(
            RestaurantManagerModule(this)
        ).build()

//        //start websocket service
//       startWebSocketService()
        createNotificationChannel()
    }
    fun getRestaurantComponent():RestaurantMangerComponent{
        return appComponent
    }
//    private fun startWebSocketService(){
//        val serviceIntent = Intent(this,WebSocketService::class.java)
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            startForegroundService(serviceIntent)
//        } else {
//            startService(serviceIntent)
//        }
//    }

    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_HIGH)
                .apply {
                    description = CHANNEL_DESCRIPTION
                }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}