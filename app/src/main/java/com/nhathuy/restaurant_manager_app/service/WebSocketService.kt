package com.nhathuy.restaurant_manager_app.service

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.ui.MainActivity
import com.nhathuy.restaurant_manager_app.util.Constants
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import kotlin.Exception

class WebSocketService :Service() {
    private var webSocketClient:WebSocketClient? = null
    // WebSocket server URL for receiving real-time admin notifications
    private val WEBSOCKET_URL  = "ws://mongodb-csvv.onrender.com/ws/admin-notifications"
    //binder for service binding
    private val binder = LocalBinder()
    //Notification manager instance
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    //Local binder class used by clients
    inner class LocalBinder : Binder(){
        fun getService(): WebSocketService = this@WebSocketService
    }
    //Called when the service is bound to an activity
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    //called when service is created
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        connectWebSocket()
        startForeground(Constants.NOTIFICATION_ID,createServiceNotification())
    }
    //keep service running until explicitly stopped
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    //establish websocket connection and define callbacks
    private fun connectWebSocket(){
        val uri = URI(WEBSOCKET_URL)
        webSocketClient = object  : WebSocketClient(uri){
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(Constants.TAG,"WebSocket connected")
            }

            override fun onMessage(message: String?) {
                message?.let {
                    try {
                        val notification = parseNotification(it)
                        handleNotification(notification)
                    }
                    catch (e:Exception){
                        Log.e(Constants.TAG,"Error parsing notification", e)
                    }
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(Constants.TAG,"Websocket closed: $reason")

                Handler(Looper.getMainLooper()).postDelayed({connectWebSocket()},5000)
            }

            override fun onError(ex: Exception?) {
                Log.d(Constants.TAG,"WebSocket error",ex)
            }
        }
        webSocketClient?.connect()
    }
    // parse the received json string into adminNotification model
    private fun parseNotification(message: String): AdminNotification {
        val gson = Gson()
        return  gson.fromJson(message,AdminNotification::class.java)
    }
    // handle received notification based on app foreground / background
    private fun handleNotification(notification: AdminNotification){
        val isAppInForeground  = isAppInForeground()
        if(isAppInForeground){
            val intent = Intent("ADMIN_NOTIFICATION_RECEIVED")
            intent.putExtra("notification",notification)

            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        }
        else{
            showSystemNotification(notification)
        }
    }

    //check if the app is running in the foreground
    private fun isAppInForeground():Boolean{
        val activityManager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        val appProcesses  = activityManager.runningAppProcesses ?: return false

        val packageName = applicationContext.packageName

        for(appProcess in appProcesses){
            if(appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName == packageName){
                return true
            }
        }
        return false
    }

    //display a system notification using NotificationManager
    private fun showSystemNotification(notification: AdminNotification){
        val intent = Intent(this, AdminNotification::class.java)
        intent.putExtra("notificationId",notification.id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent =  PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)

        val builder = NotificationCompat.Builder(this,Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notification.id.hashCode(), builder.build())
    }
    // create notification channel for android o++
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(Constants.CHANNEL_ID,"Admin Notifications",NotificationManager.IMPORTANCE_HIGH)
                .apply { description = "Notifications for restaurant admin" }

            notificationManager.createNotificationChannel(channel)
        }
    }
    // build the persistent foreground notification for the service
    private fun createServiceNotification() : Notification {
        val pendingIntent = PendingIntent.getActivity(this, 0 , Intent(this,MainActivity::class.java),PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this,Constants.CHANNEL_ID)
            .setContentTitle("Restaurant Manager")
            .setContentText("Listening for notifications")
            .setSmallIcon(R.drawable.ic_service)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    //clean up websocket when service is destroyed
    override fun onDestroy() {
        webSocketClient?.close()
        super.onDestroy()
    }
}