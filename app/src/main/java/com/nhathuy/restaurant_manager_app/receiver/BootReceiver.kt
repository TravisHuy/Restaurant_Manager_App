package com.nhathuy.restaurant_manager_app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.nhathuy.restaurant_manager_app.service.WebSocketService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action == Intent.ACTION_BOOT_COMPLETED){
            //start websocket service
            val startIntent = Intent(context, WebSocketService::class.java)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                context.startForegroundService(startIntent)
            }
            else{
                context.startService(startIntent)
            }
        }
    }

}