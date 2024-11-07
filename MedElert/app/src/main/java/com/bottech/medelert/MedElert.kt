package com.bottech.medelert

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import android.os.PowerManager

class MedElert : Application() {
    var mediaPlayer: MediaPlayer? = null
    var wakeLock: PowerManager.WakeLock? = null // Public access

    @SuppressLint("Wakelock")  // Suppress lint warning for educational purposes
    fun acquireWakeLock(context: Context) {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "AlarmReceiver:FullWakeLock")
        wakeLock?.acquire()
    }

    fun releaseWakeLock() {
        wakeLock?.release()
        wakeLock = null
    }
}