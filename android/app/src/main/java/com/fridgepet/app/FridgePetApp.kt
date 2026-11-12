package com.fridgepet.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fridgepet.app.data.local.FridgeDatabase
import com.fridgepet.app.data.remote.FirebaseAiClient
import com.fridgepet.app.data.repository.FridgeRepository
import com.fridgepet.app.worker.ExpiryCheckWorker
import java.util.concurrent.TimeUnit

class FridgePetApp : Application() {

    val database by lazy { FridgeDatabase.getInstance(this) }
    val aiClient by lazy { FirebaseAiClient() }
    val repository by lazy { FridgeRepository(database.fridgeDao(), aiClient) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleDailyExpiryCheck()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_EXPIRY_ALERTS,
                "Fridge Pet Expiry Alerts",
                NotificationManager.IMPORTANCE_DEFAULT,
            ).apply {
                description = "Alerts from your Fridge Pet when groceries are about to expire."
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun scheduleDailyExpiryCheck() {
        val request = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(
            repeatInterval = 12,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
        ).build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            WORK_NAME_EXPIRY_CHECK,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }

    companion object {
        const val CHANNEL_EXPIRY_ALERTS = "expiry_alerts"
        private const val WORK_NAME_EXPIRY_CHECK = "fridge_pet_expiry_check"
    }
}
