//package com.nhathuy.restaurant_manager_app.service
//
//import android.Manifest
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.PendingIntent
//import android.content.Context
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.os.Build
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.core.content.ContextCompat
//import com.nhathuy.restaurant_manager_app.R
//import com.nhathuy.restaurant_manager_app.data.model.AdminNotification
//import com.nhathuy.restaurant_manager_app.data.model.NotificationType
//import com.nhathuy.restaurant_manager_app.ui.MainActivity
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class NotificationAdminService @Inject constructor(private val context: Context) {
//
//    companion object {
//        private const val CHANNEL_ID = "restaurant_notifications"
//        private const val NOTIFICATION_GROUP = "restaurant_notification_group"
//    }
//
//    init {
//        createNotificationChannel()
//    }
//
//    /**
//     * create notification channel for Android O and above
//     */
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val name = context.getString(R.string.restaurant_notifications)
//            val descriptionText =
//                context.getString(R.string.notifications_for_restaurant_management)
//
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
//                description = descriptionText
//                enableLights(true)
//                enableVibration(true)
//            }
//
//            val notificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//
//    /**
//     * show a notification for the given notification
//     */
//    fun showNotification(notification: AdminNotification) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
//                PackageManager.PERMISSION_GRANTED) {
//                return
//
//                // check if context is an activity before requesting permission
////                ActivityCompat.requestPermissions(
////                    this,
////                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
////                    REQUEST_NOTIFICATION_PERMISSION
////                )
//            }
//        }
//        val intent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            putExtra("NOTIFICATION_ID", notification.id)
//        }
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )
//
//        val icon = getNotificationIcon(notification.notificationType)
//
//        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(icon)
//            .setContentTitle(notification.title)
//            .setContentText(notification.message)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .setGroup(NOTIFICATION_GROUP)
//            .setStyle(NotificationCompat.BigTextStyle().bigText(notification.message))
//
//
//
//        with(NotificationManagerCompat.from(context)) {
//            val notificationId = notification.id.hashCode()
//            notify(notificationId, notificationBuilder.build())
//        }
//    }
//
//    /**
//     * Return appropriate icon resouce based on notification type
//     */
//    private fun getNotificationIcon(type: NotificationType): Int {
//        return when (type) {
//            NotificationType.ORDER_STATUS_CHANGE -> R.drawable.ic_draf_order
//            NotificationType.NEW_RESERVATION -> R.drawable.table_2
//            NotificationType.PAYMENT_RECEIVED -> R.drawable.ic_payments
//            else -> R.drawable.ic_notification
//        }
//    }
//}