package com.nhathuy.restaurant_manager_app.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

class BackgroundNotificationWorker(appContext:Context,workerParams: WorkerParameters) : CoroutineWorker(appContext,workerParams) {

    override suspend fun doWork(): Result {
        val serviceIntent  = Intent(applicationContext, WebSocketServices::class.java)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            applicationContext.startForegroundService(serviceIntent)
        }
        else{
            applicationContext.startService(serviceIntent)
        }
        return Result.success()
    }

    companion object {
        fun sheduleWork(context: Context){
            val constrants = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest = PeriodicWorkRequestBuilder<BackgroundNotificationWorker>(
                15, TimeUnit.MINUTES,
                5, TimeUnit.MINUTES
            )
                .setConstraints(constrants)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork("AdminNotificationWork",ExistingPeriodicWorkPolicy.KEEP,workRequest)
        }
    }
}