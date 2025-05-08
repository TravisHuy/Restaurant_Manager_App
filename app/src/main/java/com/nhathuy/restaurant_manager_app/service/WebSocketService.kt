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
import com.google.gson.reflect.TypeToken
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.data.local.TokenManager
import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
import com.nhathuy.restaurant_manager_app.ui.MainActivity
import com.nhathuy.restaurant_manager_app.util.Constants
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI
import java.util.concurrent.TimeUnit
import kotlin.Exception

class WebSocketService : Service() {
    private var webSocketClient: WebSocketClient? = null
    private var reconnectAttempts = 0
    private val MAX_RECONNECT_ATTEMPTS = 5
    private val INITIAL_RECONNECT_DELAY = 5000L // 5 seconds

    // Update the WebSocket URL to match your server's endpoint
    // Make sure this is in Constants.kt so it's consistent
    private val WEBSOCKET_URL = Constants.WEBSOCKET_URL

    private val tokenManager by lazy {
        TokenManager(this)
    }

    private val binder = LocalBinder()

    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    inner class LocalBinder : Binder() {
        fun getService(): WebSocketService = this@WebSocketService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        connectWebSocket()
        startForeground(Constants.NOTIFICATION_ID, createServiceNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (webSocketClient == null || webSocketClient?.isOpen == false) {
            reconnectAttempts = 0 // Reset reconnect attempts on manual start
            connectWebSocket()
        }
        return START_STICKY
    }

    private fun connectWebSocket() {
        try {
            val uri = URI(WEBSOCKET_URL)
            Log.d(Constants.TAG, "Attempting to connect to WebSocket at: $WEBSOCKET_URL")

            // Create WebSocket client with authentication headers
            webSocketClient = object : WebSocketClient(uri) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    Log.d(
                        Constants.TAG,
                        "WebSocket connected - HTTP Status: ${handshakedata?.httpStatus}, Status Message: ${handshakedata?.httpStatusMessage}"
                    )
                    reconnectAttempts = 0 // Reset reconnect attempts on successful connection

                    // Send a message to request notifications - match the server's expected format
                    // For direct WebSocket handler, this may work:
                    send("getNotifications")

                    // For STOMP protocol, you would need proper STOMP framing:
                    // sendStompConnect() - if you're using STOMP
                }

                override fun onMessage(message: String?) {
                    Log.d(Constants.TAG, "Received WebSocket message: $message")
                    handleWebSocketMessage(message)
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    Log.d(
                        Constants.TAG,
                        "WebSocket closed: Code: $code, Reason: $reason, Remote: $remote"
                    )

                    // Implement exponential backoff for reconnection
                    if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                        val delay = INITIAL_RECONNECT_DELAY * (1 shl reconnectAttempts)
                        reconnectAttempts++

                        Log.d(
                            Constants.TAG,
                            "Attempting to reconnect in ${delay}ms (Attempt $reconnectAttempts of $MAX_RECONNECT_ATTEMPTS)"
                        )
                        Handler(Looper.getMainLooper()).postDelayed({ connectWebSocket() }, delay)
                    } else {
                        Log.e(Constants.TAG, "Maximum reconnection attempts reached")
                    }
                }

                override fun onError(ex: Exception?) {
                    Log.e(Constants.TAG, "WebSocket error", ex)
                    webSocketClient?.close()
                }
            }

            // Add JWT token to request header if available
            val headers = mutableMapOf<String, String>()
            tokenManager.getAccessToken()?.let { token ->
                Log.d(Constants.TAG, "Adding auth token to WebSocket connection")
                headers["Authorization"] = "Bearer $token"
                webSocketClient?.addHeader("Authorization", "Bearer $token")
            }

            // Connect with timeout
            val connected = webSocketClient?.connectBlocking(10, TimeUnit.SECONDS)
            Log.d(Constants.TAG, "WebSocket connect result: $connected")

            if (connected != true) {
                Log.e(Constants.TAG, "Failed to connect to WebSocket within timeout")
                Handler(Looper.getMainLooper()).postDelayed(
                    { connectWebSocket() },
                    INITIAL_RECONNECT_DELAY
                )
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Error connecting to WebSocket", e)

            // Implement exponential backoff for reconnection after exceptions too
            if (reconnectAttempts < MAX_RECONNECT_ATTEMPTS) {
                val delay = INITIAL_RECONNECT_DELAY * (1 shl reconnectAttempts)
                reconnectAttempts++

                Log.d(
                    Constants.TAG,
                    "Attempting to reconnect in ${delay}ms (Attempt $reconnectAttempts of $MAX_RECONNECT_ATTEMPTS)"
                )
                Handler(Looper.getMainLooper()).postDelayed({ connectWebSocket() }, delay)
            }
        }
    }

    private fun handleWebSocketMessage(message: String?) {
        message?.let {
            try {
                if (it.startsWith("[")) {
                    // Handle an array of notifications
                    val type = object : TypeToken<List<AdminNotification>>() {}.type
                    val notifications = Gson().fromJson<List<AdminNotification>>(it, type)
                    Log.d(Constants.TAG, "Parsed ${notifications.size} notifications")

                    if (notifications.isNotEmpty()) {
                        for (notification in notifications) {
                            handleNotification(notification)
                        }
                    }
                    else{

                    }
                } else if (it == "pong") {
                    // Handle ping response
                    Log.d(Constants.TAG, "Received pong response")
                } else {
                    // Try to parse as a single notification
                    try {
                        val notification = parseNotification(it)
                        handleNotification(notification)
                    } catch (e: Exception) {
                        Log.e(Constants.TAG, "Error parsing single notification: $it", e)
                        Log.d(Constants.TAG, "Raw message content: $it")
                    }
                }
            } catch (e: Exception) {
                Log.e(Constants.TAG, "Error handling WebSocket message", e)
                Log.d(Constants.TAG, "Raw message content: $it")
            }
        }
    }

    private fun parseNotification(message: String): AdminNotification {
        val gson = Gson()
        return gson.fromJson(message, AdminNotification::class.java)
    }

    private fun handleNotification(notification: AdminNotification) {
        val isAppInForeground = isAppInForeground()
        Log.d(
            Constants.TAG,
            "Notification received: ${notification.title}. App in foreground: $isAppInForeground"
        )

        if (isAppInForeground) {
            val intent = Intent("ADMIN_NOTIFICATION_RECEIVED")
            intent.putExtra("notification", notification)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
        } else {
            showSystemNotification(notification)
        }
    }

    // Rest of the code remains the same...

    private fun isAppInForeground(): Boolean {
        val activityManager =
            applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = applicationContext.packageName

        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                appProcess.processName == packageName
            ) {
                return true
            }
        }
        return false
    }

    private fun showSystemNotification(notification: AdminNotification) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("notificationId", notification.id)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        val builder = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notification.title)
            .setContentText(notification.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(notification.id.hashCode(), builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                Constants.CHANNEL_ID,
                "Admin Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for restaurant admin"
            }

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createServiceNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setContentTitle("Restaurant Manager")
            .setContentText("Listening for notifications")
            .setSmallIcon(R.drawable.ic_service)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    override fun onDestroy() {
        webSocketClient?.close()
        super.onDestroy()
    }
}