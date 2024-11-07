package com.bottech.medelert.notificationReminder

import AlarmDatabaseHelper
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.bottech.medelert.MedElert

class TakenReceiver : BroadcastReceiver() {

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

        Log.d("AlarmReceiver", "Taken button pressed for millisId: $millisId")  // Updated tag

        Toast.makeText(context, "Medication Taken", Toast.LENGTH_SHORT).show()

        val alarmDatabaseHelper = AlarmDatabaseHelper(context)
        alarmDatabaseHelper.updateAlarmStatus(millisId, isTaken = true, isMissed = false, isSkipped = false)

        // Update database with "Taken" status for alarmId
        // ... (Database update logic)
        // Cancel the alarm and dismiss the notification

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager?.cancel(millisId)
    }
}