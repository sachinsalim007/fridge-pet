package com.fridgepet.app.worker

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fridgepet.app.FridgePetApp
import com.fridgepet.app.MainActivity
import com.fridgepet.app.data.local.FridgeDatabase
import com.fridgepet.app.domain.PetMoodEngine

class ExpiryCheckWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val dao = FridgeDatabase.getInstance(applicationContext).fridgeDao()
        val profile = dao.getPetProfileOnce() ?: return Result.success()
        if (!profile.onboarded) return Result.success()

        val items = dao.getActiveItemsOnce()
        val expired = items.filter { PetMoodEngine.daysUntilExpiry(it.expiryEpochDay) < 0 }
        val soon = items.filter { PetMoodEngine.daysUntilExpiry(it.expiryEpochDay) in 0..2 }

        if (expired.isEmpty() && soon.isEmpty()) {
            return Result.success()
        }

        val canNotify = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        if (!canNotify) return Result.success()

        val title = if (expired.isNotEmpty()) {
            "${profile.name} is feeling sick!"
        } else {
            "${profile.name} is getting anxious!"
        }

        val body = buildString {
            if (expired.isNotEmpty()) {
                append("${expired.size} item(s) expired (${expired.first().name}). ")
            }
            if (soon.isNotEmpty()) {
                append("${soon.size} item(s) expiring within 2 days: ${soon.take(3).joinToString { it.name }}.")
            }
        }.trim()

        val tapIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            1001,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(
            applicationContext,
            FridgePetApp.CHANNEL_EXPIRY_ALERTS,
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(2001, notification)
        return Result.success()
    }
}
