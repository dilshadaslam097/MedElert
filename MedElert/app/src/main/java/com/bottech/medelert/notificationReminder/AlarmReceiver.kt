package com.bottech.medelert.notificationReminder

import AlarmDatabaseHelper
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.bottech.medelert.MedElert
import com.bottech.medelert.R
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val millisId = intent.getIntExtra("millisId", -1)
        val timeInMillis = intent.getLongExtra("timeInMillis", -2L)

        if (millisId == -1) {
            Log.d("AlarmReceiver", "Invalid millisId received, skipping.")
            return  // Handle invalid millisId
        }

        val currentDateTime = Calendar.getInstance().timeInMillis

        // Check if alarm time has passed 1 minute before current time
        val oneMinuteInMillis = 1 * 60 * 1000L
        if (currentDateTime - timeInMillis > oneMinuteInMillis) {
            // Alarm time is in the past (more than 1 minute ago)
            val alarmDatabaseHelper = AlarmDatabaseHelper(context)
            alarmDatabaseHelper.updateAlarmStatus(millisId, isTaken = true, isMissed = true, isSkipped = true)
            Log.d("AlarmReceiver", "Alarm time is in the past, marked as missed.")
            return
        }

        // Check alarm status from database before creating notification
        val alarmDatabaseHelper = AlarmDatabaseHelper(context)
        val alarmStatus = alarmDatabaseHelper.getAlarmStatus(millisId)

        if (alarmStatus.isTaken || alarmStatus.isMissed || alarmStatus.isSkipped || !alarmStatus.isEnabled) {
            // Alarm already taken, skipped, missed, or disabled - don't play alarm or show notification
            Log.d("AlarmReceiver", "Alarm already handled or disabled, skipping notification.")
            return
        }

        // Create notification channel (for Android 8.0+)
        createNotificationChannel(context)

        // Acquire wake lock from MedElert
        (context.applicationContext as MedElert).acquireWakeLock(context)

        // Create notification
        val notificationBuilder = NotificationCompat.Builder(context, "alarm_channel")
            .setContentTitle("Medication Reminder")
            .setContentText("It's time to take your medication.")
            .setSmallIcon(R.drawable.alarm_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setFullScreenIntent(createFullScreenPendingIntent(context, millisId), true)
            .addAction(R.drawable.istaken_ic, "Taken", PendingIntent.getBroadcast(context, millisId, Intent(context, TakenReceiver::class.java).putExtra("millisId", millisId), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))
            .addAction(R.drawable.isskipped_ic, "Skip", PendingIntent.getBroadcast(context, millisId, Intent(context, SkipReceiver::class.java).putExtra("millisId", millisId), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT))

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.notify(millisId, notificationBuilder.build())
        Log.d("AlarmReceiver", "Notification shown for alarm with millisId: $millisId")  // Log notification display

        // Create and start media player /Play a custom sound
        playCustomSound(context)

        // Schedule alarm to stop ringing after 15 seconds
        val stopRingIntent = Intent(context, StopRingReceiver::class.java)
        stopRingIntent.putExtra("millisId", millisId)
        val stopRingPendingIntent = PendingIntent.getBroadcast(context, millisId, stopRingIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 15000, stopRingPendingIntent)
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Alarm Channel"
            val description = "Alarm notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarm_channel", name, importance)
            channel.description = description
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
            Log.d("AlarmReceiver", "Notification channel created.")  // Log channel creation
        }
    }

    private fun createFullScreenPendingIntent(context: Context, millisId: Int): PendingIntent {
        val intent = Intent(context, FullScReminder::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("millisId", millisId)
        return PendingIntent.getActivity(context, millisId, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
    }
    private fun playCustomSound(context: Context) {
        val application = context.applicationContext as MedElert
        application.mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound).apply {
            isLooping = true
            start()
        }
    }
}