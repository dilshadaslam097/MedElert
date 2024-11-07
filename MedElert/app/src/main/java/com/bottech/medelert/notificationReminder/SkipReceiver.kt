package com.bottech.medelert.notificationReminder

import AlarmDatabaseHelper
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.bottech.medelert.MedElert

class SkipReceiver : BroadcastReceiver() {

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

        Log.d("AlarmReceiver", "Skip button pressed for millisId: $millisId")

        Toast.makeText(context, "Medication Skipped", Toast.LENGTH_SHORT).show()

        val alarmDatabaseHelper = AlarmDatabaseHelper(context)
        alarmDatabaseHelper.updateAlarmStatus(millisId, isTaken = false, isMissed = false, isSkipped = true)

        // Update database with "Skipped" status for alarmId
        // ... (Database update logic)
        // Cancel the alarm and dismiss the notification

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.cancel(millisId)
    }
}