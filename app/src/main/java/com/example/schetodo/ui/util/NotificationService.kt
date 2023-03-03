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

        val contentText =
            if (todoDescriptions.isNotEmpty())
                formatToListWithDotsString(todoDescriptions.subList(0, 1)).toString()
            else scheduleBlock.todoBlock.notes ?: ""

        val bigContentText =
            formatToListWithDotsString(todoDescriptions).toString() + "\n" + scheduleBlock.todoBlock.notes

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
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(contentTitle)
            .setContentText(if (contentText != bigContentText) "$contentText..." else contentText)
            .setContentIntent(getOpenMainActivityIntent())
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigContentText))
        icon?.let { notification.setLargeIcon(it) }

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