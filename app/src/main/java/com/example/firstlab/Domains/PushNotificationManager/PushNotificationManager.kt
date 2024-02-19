package com.example.firstlab.Domains.PushNotificationManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.firstlab.Activitis.MainActivity
import com.example.firstlab.R

class PushNotificationManager(private val context: Context) {

    companion object {
        private const val CHANNEL_ID_ERROR = "Error_Channel"
        private const val CHANNEL_NAME_ERROR = "Error Notifications"
        private const val NOTIFICATION_ID_ERROR = 101

        private const val CHANNEL_ID_LEVEL = "Level_Channel"
        private const val CHANNEL_NAME_LEVEL = "Level Notifications"
        private const val NOTIFICATION_ID_LEVEL = 102
    }

    init {
        createErrorNotificationChannel()
        createLevelNotificationChannel()
    }

    fun sendErrorNotification(errorMessage: String) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            createNotificationChannel(CHANNEL_ID_ERROR, CHANNEL_NAME_ERROR)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID_ERROR)
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle("Ошибка")
                .setContentText(errorMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

            val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setFullScreenIntent(fullScreenPendingIntent, true)

            try {
                with(NotificationManagerCompat.from(context)) {
                    notify(NOTIFICATION_ID_ERROR, builder.build())
                }
            } catch (e: SecurityException) {
            }
        }
    }

    fun sendLevelNotification(levelMessage: String) {
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            createNotificationChannel(CHANNEL_ID_LEVEL, CHANNEL_NAME_LEVEL)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val builder = NotificationCompat.Builder(context, CHANNEL_ID_LEVEL)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Уведомление из уровня")
                .setContentText(levelMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

            val fullScreenPendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            builder.setFullScreenIntent(fullScreenPendingIntent, true)

            try {
                with(NotificationManagerCompat.from(context)) {
                    notify(NOTIFICATION_ID_LEVEL, builder.build())
                }
            } catch (e: SecurityException) {
            }
        }
    }

    private fun createErrorNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_ERROR, CHANNEL_NAME_ERROR, importance).apply {
                description = "Канал для уведомлений об ошибках"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createLevelNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_LEVEL, CHANNEL_NAME_LEVEL, importance).apply {
                description = "Канал для уведомлений уровня"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotificationChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = if (channelId == CHANNEL_ID_ERROR) {
                    "Канал для уведомлений об ошибках"
                } else {
                    "Канал для уведомлений уровня"
                }
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
