package com.example.texnostrelka_2025_otbor

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.texnostrelka_2025_otbor.data.local.preferences.PreferencesManager
import com.example.texnostrelka_2025_otbor.data.remote.model.user.notification.UserNotificationTokenModel
import com.example.texnostrelka_2025_otbor.data.remote.repository.NetworkRepository
import com.example.texnostrelka_2025_otbor.presentation.ui.infocomic.InfoComicActivity
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
        const val COMICS_ID = "COMICS_ID"
        const val NOTIFICATION_TYPE = "type"
        const val NOTIFICATION_TYPE_NEW_COMIC = "new_comic"
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

        remoteMessage.data.let { data ->
            if (data.isNotEmpty()) {
                Log.d(TAG, "Message data: $data")
                when (data[NOTIFICATION_TYPE]) {
                    NOTIFICATION_TYPE_NEW_COMIC -> {
                        val comicsId = data["comic_id"] ?: ""
                        val title = remoteMessage.notification?.title ?: "Новый комикс"
                        val message = remoteMessage.notification?.body ?: "Доступен новый комикс"

                        createComicNotification(
                            title = title,
                            message = message,
                            comicsId = comicsId
                        )
                    }
                    else -> {
                        remoteMessage.notification?.let { notification ->
                            Log.d(TAG, "Notification received: ${notification.title} - ${notification.body}")
                            createNotification(
                                title = notification.title ?: "Уведомление",
                                message = notification.body ?: "Новое уведомление"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createComicNotification(title: String, message: String, comicsId: String) {
        try {
            val comicIntent = Intent(this, InfoComicActivity::class.java).apply {
                putExtra(COMICS_ID, comicsId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            val pendingIntent = TaskStackBuilder.create(this).apply {
                addNextIntentWithParentStack(comicIntent)
            }.getPendingIntent(
                comicsId.hashCode(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                else
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

            val channelId = "comics_notifications"
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
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
            Log.d(TAG, "Comic notification created: $title, comicsId: $comicsId")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating comic notification", e)
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

            val channelId = "default_notifications"
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    channelId,
                    "Default Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Channel for default notifications"
                }
                notificationManager.createNotificationChannel(channel)
            }

            notificationManager.notify(Random.nextInt(), notificationBuilder.build())
            Log.d(TAG, "Default notification created: $title")
        } catch (e: Exception) {
            Log.e(TAG, "Error creating default notification", e)
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