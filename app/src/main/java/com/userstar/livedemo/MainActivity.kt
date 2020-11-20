package com.userstar.livedemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.userstar.livedemo.ui.main.view.MainFragment
import com.userstar.phonekeyblelockdemokotlin.timber.ReleaseTree
import com.userstar.phonekeyblelockdemokotlin.timber.ThreadIncludedDebugTree
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        if (savedInstanceState == null) {

            if (BuildConfig.DEBUG) {
                Timber.plant(ThreadIncludedDebugTree())
            } else {
                Timber.plant(ReleaseTree())
            }

            FirebaseMessaging.getInstance()
                .subscribeToTopic("LiveDemo")
                .addOnCompleteListener { task ->
                    Timber.i("FCM subscribe topic: ${task.isSuccessful}")
                }

            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.instance())
                    .commitNow()
        }
    }

    enum class Orientation {
        LANDSCAPE,
        PORTRAIT
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(false)
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
            }
            supportActionBar!!.hide()
            EventBus.getDefault().post(Orientation.LANDSCAPE)
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                window.setDecorFitsSystemWindows(true)
            } else {
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            }
            supportActionBar!!.show()
            EventBus.getDefault().post(Orientation.PORTRAIT)
        }
    }

    internal object CustomNotification {
        private var CHANNEL_ID: String? = null
        private var idCount = 0
        fun configure(
            channelID: String?,
            context: Context?
        ) {
            CHANNEL_ID = channelID
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_HIGH)
                channel.description = CHANNEL_ID
                channel.enableLights(true)
                channel.enableVibration(true)
                NotificationManagerCompat.from(context!!).createNotificationChannel(channel)
            }
        }

        fun showNotification(
            context: Context,
            activityClass: Class<*>?,
            title: String?,
            body: String?
        ) {
            var pendingIntent: PendingIntent? = null
            if (activityClass != null) {
                //Create an explicit intent for an Activity in your app
                val intent = Intent(context, activityClass)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP) // Clear
                pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT)
            }
            NotificationManagerCompat.from(context).notify(
                idCount++,
                NotificationCompat.Builder(context, CHANNEL_ID!!)
                    .setShowWhen(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(title)
                    .setContentText(body)
                    .setContentIntent(pendingIntent)
                    .setChannelId(CHANNEL_ID!!)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build()
            )
        }
    }
}
