package com.nhathuy.restaurant_manager_app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.model.WebSocketConnectionState
import com.nhathuy.restaurant_manager_app.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class WebSocketService : Service() {

    @Inject
    lateinit var webSocketClient: RestaurantWebSocketClient

    @Inject
    lateinit var notificationService: NotificationAdminService
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val baseUrl = Constants.WEBSOCKET_URL

    override fun onCreate() {
        super.onCreate()

        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)

        // Start as a foreground service
        startForeground()
        //Connect to WebSocket
        webSocketClient.connect(baseUrl)

        //start collecting notifications
        collectNotifications()

        //monior connection state
        moniorConnectionState()
    }

    private fun startForeground() {
        //create a notifiction channel
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channelId = "restaurant_websocket_service"
            val channel = NotificationChannel(
                channelId,
                "Restaurant Websocket Service",
                NotificationManager.IMPORTANCE_LOW
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this,channelId)
                .setContentTitle("Restaurant Manger")
                .setContentText("Receving restaurant notificaitons")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build()
            startForeground(1001,notification)
        }
    }
    private fun collectNotifications(){
        serviceScope.launch {
            webSocketClient.notificationFlow.collect {
                notification ->
                notificationService.showNotification(notification)
            }
        }
    }
    private fun moniorConnectionState(){
        serviceScope.launch {
            webSocketClient.connectionState.collectLatest {
                state ->

                when(state){
                    WebSocketConnectionState.DISCONNECTED -> {
                        serviceScope.launch {
                            delay(5000)
                            webSocketClient.connect(baseUrl)
                        }
                    }
                    WebSocketConnectionState.ERROR -> {
                        serviceScope.launch {
                            delay(10000)
                            webSocketClient.connect(baseUrl)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        webSocketClient.disconnect()
        serviceScope.cancel()
    }

}