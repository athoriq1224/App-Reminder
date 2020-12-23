package com.google.developers.lettervault.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat.getSystemService
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.developers.lettervault.R
import com.google.developers.lettervault.ui.detail.LetterDetailActivity
import com.google.developers.lettervault.util.LETTER_ID
import com.google.developers.lettervault.util.NOTIFICATION_CHANNEL_ID
import com.google.developers.lettervault.util.NOTIFICATION_ID

/**
 * Run a work to show a notification on a background thread by the {@link WorkManager}.
 */
class NotificationWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    /**
     * Create an intent with extended data to the letter.
     */
    private fun getContentIntent(letterID : Long): PendingIntent? {
        val intent = Intent(applicationContext, LetterDetailActivity::class.java).apply {
            putExtra(LETTER_ID, letterID)
        }
        return TaskStackBuilder.create(applicationContext).run {
            addNextIntentWithParentStack(intent)
            return@run getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    override fun doWork(): Result {
        val prefManager = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val shouldNotify = prefManager.getBoolean(
            applicationContext.getString(R.string.pref_key_notify),
            false
        )
        if (shouldNotify) {
            applicationContext.run {
                val letterId: Long = inputData.getLong(LETTER_ID, 0)
                val intent = getContentIntent(letterId)
                val notificationManager = getSystemService(this, NotificationManager::class.java) as NotificationManager
                val notificationBuilder =
                    NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_mail)
                        .setContentTitle(getString(R.string.notify_title))
                        .setContentText(getString(R.string.notify_content))
                        .setContentIntent(intent)
                        .setAutoCancel(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.notify_channel_name), NotificationManager.IMPORTANCE_DEFAULT)
                    notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
                    notificationManager.createNotificationChannel(channel)
                }
                val notification = notificationBuilder.build()
                notificationManager.notify(NOTIFICATION_ID, notification)
            }
        }
        return Result.success()
    }
}
