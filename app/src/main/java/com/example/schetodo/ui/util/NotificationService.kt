package com.example.schetodo.ui.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.core.app.NotificationCompat
import com.example.schetodo.R
import com.example.schetodo.data.schedule_block.ScheduleBlock
import com.example.schetodo.ui.MainActivity

class NotificationService(
    private val context: Context
) {
    companion object {
        const val SCHEDULE_NOTIFICATION_CHANNEL_ID = "schedule_notification_channel"
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showScheduleNotification(scheduleBlock: ScheduleBlock, notificationId: Int) {
        val contentTitle = scheduleBlock.todoCategories.joinToString(", ") { it.name }
        val todoDescriptions = scheduleBlock.todos.map { it.description }

        val contentText = appendDotsToStrings(
            strings = todoDescriptions, separator = "\t\t"
        ).toString() + "\t\t" + scheduleBlock.todoBlock.notes

        val bigContentText = appendDotsToStrings(
            strings = todoDescriptions, separator = "\n"
        ).toString() + "\n" + scheduleBlock.todoBlock.notes

        showNotification(
            contentTitle = contentTitle,
            contentText = contentText,
            bigContentText = bigContentText,
            notificationId = notificationId
        ) // TODO pass icon of category
    }

    private fun showNotification(
        contentTitle: String,
        contentText: String,
        bigContentText: String,
        notificationId: Int,
        icon: Bitmap? = null
    ) {
        val notification = NotificationCompat.Builder(context, SCHEDULE_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.schetodo_logo_foreground)
            .setContentIntent(getOpenMainActivityIntent())
            .setAutoCancel(true)

        if (contentTitle.isNotEmpty())
            notification.setContentTitle(contentTitle)
        if (contentText.isNotEmpty())
            notification.setContentText(contentText)
        if (bigContentText.isNotEmpty())
            notification.setStyle(NotificationCompat.BigTextStyle().bigText(bigContentText))
        if (icon != null)
            notification.setLargeIcon(icon)

        notificationManager.notify(notificationId, notification.build())
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun getOpenMainActivityIntent() =
        PendingIntent.getActivity(
            context,
            1,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
}