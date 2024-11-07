package com.bottech.medelert.notificationReminder

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmManagerHelper {

    @SuppressLint("ScheduleExactAlarm")
    fun setupAlarm(context: Context, millisId: Int, timeInMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("millisId", millisId) // Add millisId to the intent
        intent.putExtra("timeInMillis", timeInMillis) // Add timeInMillis to the intent

        val pendingIntent = PendingIntent.getBroadcast(context, millisId, intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    fun cancelAlarm(context: Context, millisId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("millisId", millisId)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            millisId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
//
//    fun setupPeriodicAlarm(context: Context, timeInMillisList: List<Long>, datesTimesList: List<String>) {
//        val intent = Intent(context, AlarmReceiver::class.java).apply {
//            putExtra(ALARM_TEXT, Gson().toJson(dateTimeMillis))
//        }
//
//        val pendingIntent = PendingIntent.getBroadcast(
//            context, dateTimeMillis.timeInMillis.toInt(),
//            intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
//        )
//        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        try {
//            val interval = 5L * 60L * 1000L
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, dateTimeMillis.timeInMillis, interval, pendingIntent)
//        } catch (e:SecurityException){
//            e.printStackTrace()
//        }
//    }
}