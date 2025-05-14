//package com.nhathuy.restaurant_manager_app.receiver
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import com.nhathuy.restaurant_manager_app.data.local.TokenManager
//import com.nhathuy.restaurant_manager_app.service.BackgroundNotificationWorker
//
//class BootCompletedReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        if(intent.action  == Intent.ACTION_BOOT_COMPLETED){
//            val tokenManager  = TokenManager(context)
//            val isLoggedIn = tokenManager.isUserLoggedIn()
//            val isAdmin = tokenManager.getUserRole() == "ROLE_ADMIN"
//
//            if(isLoggedIn && isAdmin){
//                BackgroundNotificationWorker.sheduleWork(context)
//            }
//        }
//    }
//}