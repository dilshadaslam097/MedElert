package com.bottech.medelert.notificationReminder

import AlarmDatabaseHelper
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import com.bottech.medelert.MedElert

class StopRingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val millisId = intent.getIntExtra("millisId", -1)
        if (millisId == -1) {
            Log.d("AlarmReceiver", "Invalid millisId received, skipping.")
            return  // Handle invalid millisId
        }
        val application = context.applicationContext as MedElert
        application.mediaPlayer?.stop()
        application.mediaPlayer?.release()
        application.mediaPlayer = null

//        (context.applicationContext as MedElert).acquireWakeLock(context)
//        wakeLock?.release()
        application.wakeLock?.release()


        // Check alarm status from database before creating notification
        val alarmDatabaseHelper = AlarmDatabaseHelper(context)
        val alarmStatus = alarmDatabaseHelper.getAlarmStatus(millisId)

        if (!alarmStatus.isTaken && !alarmStatus.isSkipped) {
            Toast.makeText(context, "Medication Missed", Toast.LENGTH_SHORT).show()
            alarmDatabaseHelper.updateAlarmStatus(millisId, isTaken = false, isMissed = true, isSkipped = false)
            Log.d("AlarmReceiver", "Medication Missed for millisId: $millisId")  // Updated tag
        }

        // Cancel the alarm and dismiss the notification
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.cancel(millisId)
        Log.d("AlarmReceiver", "Stopped notification sound for millisId: $millisId")

    }
}