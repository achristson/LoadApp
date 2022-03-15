package com.udacity

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0

@SuppressLint("UnspecifiedImmutableFlag")
fun NotificationManager.sendNotification(context: Context, message: String, fileToDownload: String, status: String){

    val seeChangesIntent = Intent(context, DetailActivity::class.java)
                                .putExtra("filename", fileToDownload)
                                .putExtra("status", status)

    val seeChangesPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        seeChangesIntent,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val builder = NotificationCompat.Builder(
        context,
        context.getString(R.string.channelID))
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(context.getString(R.string.notification_title))
        .setContentText(message)
        .setAutoCancel(true)
        .addAction(
            R.drawable.ic_launcher_background,
            context.getString(R.string.notification_button),
            seeChangesPendingIntent)
        .setPriority(NotificationCompat.PRIORITY_LOW)

    notify(NOTIFICATION_ID, builder.build())
}