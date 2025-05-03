package com.example.texnostrelka_2025_otbor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.model.user.notification.UserNotificationTokenModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.main.MainContainerActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    companion object {
        private const val TAG = "FirebaseMsgService"
    }

    @Inject lateinit var networkRepository: NetworkRepository
    @Inject lateinit var preferencesManager: PreferencesManager

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Service created")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "New FCM token: $token")
        sendFcmTokenToServer(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        remoteMessage.notification?.let { notification ->
            Log.d(TAG, "Notification received: ${notification.title} - ${notification.body}")
            createNotification(
                title = notification.title ?: "Новый комикс",
                message = notification.body ?: "Доступен новый комикс"
            )
        }
        remoteMessage.data.let { data ->
            if (data.isNotEmpty()) {
                Log.d(TAG, "Message data: $data")
            }
        }
    }

    private fun createNotification(title: String, message: String) {
        try {
            val intent = Intent(this, MainContainerActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("from_notification", true)
            }

            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

            val pendingIntent = PendingIntent.getActivity(
                this,
                Random.nextInt(),
                intent,
                pendingIntentFlags
            )

            val channelId = "comics_notifications"
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_global)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Comics Notifications",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channel for comics notifications"
                }
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(Random.nextInt(), notificationBuilder.build())
            Log.d(TAG, "Notification created: $title")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating notification", e)
        }
    }

    private fun sendFcmTokenToServer(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d(TAG, "Attempting to send FCM token to server")

                val authToken = preferencesManager.getAuthToken()
                if (authToken == null) {
                    Log.w(TAG, "Auth token is null, cannot send FCM token")
                    return@launch
                }

                Log.d(TAG, "Sending FCM token with auth token: ${authToken.take(5)}...")

                networkRepository.postNotificationToken(
                    token = "Bearer $authToken",
                    request = UserNotificationTokenModel(token)
                ).also {
                    Log.d(TAG, "FCM token successfully sent to server")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error sending FCM token to server", e)
                preferencesManager.savePendingFcmToken(token)
            }
        }
    }
}