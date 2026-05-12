package com.example.lab5.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.lab5.MainActivity
import com.example.lab5.R
import com.example.lab5.profile.FirebaseUserProfileService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PushMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        val tokenStorage = FcmTokenStorage(this)
        tokenStorage.save(token)
        FirebaseUserProfileService(tokenStorage).updateTokenIfUserExists(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message from: ${message.from}, data: ${message.data}")

        val title = message.data["title"]
            ?: message.notification?.title
            ?: "BookSpace"
        val body = message.data["body"]
            ?: message.notification?.body
            ?: "Новое уведомление"

        showNotification(
            title = title,
            body = body,
            data = message.data
        )
    }

    private fun showNotification(
        title: String,
        body: String,
        data: Map<String, String>
    ) {
        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            data.forEach { (key, value) -> putExtra(key, value) }
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            data.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Основные уведомления",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Push-уведомления BookSpace"
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private companion object {
        const val TAG = "PushMessagingService"
        const val CHANNEL_ID = "bookspace_push_default"
    }
}
