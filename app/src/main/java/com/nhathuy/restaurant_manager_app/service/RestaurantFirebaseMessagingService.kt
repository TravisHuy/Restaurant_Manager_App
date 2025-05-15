package com.nhathuy.restaurant_manager_app.service

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nhathuy.restaurant_manager_app.R
import com.nhathuy.restaurant_manager_app.RestaurantMangerApp
import com.nhathuy.restaurant_manager_app.data.api.NotificationService
import com.nhathuy.restaurant_manager_app.data.local.SessionManager
import com.nhathuy.restaurant_manager_app.data.model.FcmTokenRequest
import com.nhathuy.restaurant_manager_app.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RestaurantFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var sessionManager: SessionManager

    @Inject
    lateinit var notificationService: NotificationService

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object {
        private const val CHANNEL_ID = "restaurant_notification_channel"
        private const val CHANNEL_NAME = "Restaurant Notifications"
        private const val CHANNEL_DESCRIPTION = "Notifications for Restaurants"

        const val NOTIFICATION_RECEIVED = "com.nhathuy.restaurant_manager_app.NOTIFICATION_RECEIVED"

        // Add a method to register token that can be called from other parts of the app
        fun registerToken(context: Context, token: String?) {
            if (token == null) return

            val app = context.applicationContext as RestaurantMangerApp
            val notificationService = app.getRestaurantComponent().getNotificationService()
            val sessionManager = app.getRestaurantComponent().getSessionManager()

            if (sessionManager.isLoggedIn.value) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val tokenRequest = FcmTokenRequest(token)
                        notificationService.updateFcmToken(tokenRequest)
                        Log.d("FCM", "Token registered successfully: $token")
                    } catch (e: Exception) {
                        Log.e("FCM", "Error registering token: ${e.message}")
                    }
                }
            } else {
                Log.d("FCM", "User not logged in, token registration skipped")
            }
        }
    }

    /**
     * Called when a new FCM token is generated or updated
     */
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "Refreshed token: $token")
        scope.launch {
            registerTokenWithServer(token)
        }
    }

    override fun onCreate() {
        super.onCreate()
        (application as RestaurantMangerApp).getRestaurantComponent().inject(this)
    }

    private fun registerTokenWithServer(token: String) {
        if (sessionManager.isLoggedIn.value) {
            // this should send the token to your backend userfcmcontroller
            scope.launch {
                try {
                    val tokenRequest = FcmTokenRequest(token)
                    val response = notificationService.updateFcmToken(tokenRequest)
                    if (response.isSuccessful) {
                        Log.d("FCM", "Token registered successfully: $token")
                    } else {
                        Log.e("FCM", "Failed to register token: ${response.code()}")
                    }
                }
                catch (e: Exception) {
                    Log.e("FCM", "Error registering token: ${e.message}")
                }
            }
        } else {
            Log.d("FCM", "User not logged in, token registration skipped")
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val title = message.notification?.title ?: message.data["title"] ?: "New Notification"
        val body = message.notification?.body ?: message.data["message"] ?: ""
        val type = message.data["type"] ?: ""
        val relateId = message.data["relatedId"] ?: ""

        //create notification data object
        val notificationData = HashMap<String, String>().apply {
            put("title", title)
            put("body", body)
            put("type", type)
            put("related", relateId)
        }

        val isAppInForeground = isAppInForeground()

        if (isAppInForeground) {
            sendForegroundNotification(notificationData)
        } else {
            showBackgroundNotification(title, body, type, relateId)
        }
    }


    /**
     * Send notification to foreground activity
     */
    private fun sendForegroundNotification(data: HashMap<String, String>) {
        val intent = Intent(NOTIFICATION_RECEIVED).apply {
            putExtra("data", data)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    /**
     * show system notification when app is in background
     */
    private fun showBackgroundNotification(title: String, body: String, type: String, relatedId: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("type", type)
            putExtra("relatedId", relatedId)
        }

        val pendingIntentFlag = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, pendingIntentFlag
        )
        //build notification
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)


        //show notification
        val notificationId = System.currentTimeMillis().toInt()
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /**
     * create notification channel for android O and above
     */
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Check if app is in foreground
     */
    private fun isAppInForeground(): Boolean {
        val appProcessInfo = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(appProcessInfo)
        return appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND ||
                appProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE
    }
}