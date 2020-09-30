package com.userstar.livedemo.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.userstar.livedemo.ui.main.viewModel.Review
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class FCMService : FirebaseMessagingService() {

    override fun onCreate() {
        super.onCreate()

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val dataMap = remoteMessage.data
        Timber.i("message received: $dataMap")

        EventBus.getDefault().post(
            Review(
                dataMap["title"] ?: "NULL",
                dataMap["time"] ?: "NULL",
                dataMap["id"] ?: "NULL",
                dataMap["thumbnailUri"] ?: "NULL"
            )
        )
    }
}