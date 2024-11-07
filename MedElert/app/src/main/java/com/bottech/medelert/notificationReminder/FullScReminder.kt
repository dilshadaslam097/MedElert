package com.bottech.medelert.notificationReminder

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bottech.medelert.R

class FullScReminder : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_sc_reminder)

        // Display reminder message on the full screen
        val reminderTextView = findViewById<TextView>(R.id.reminder_text)
        reminderTextView.text = "It's time to take your medication!"

        // Get the millisId from the intent
        val millisId = intent.getIntExtra("millisId", -1)
        if (millisId == -1) {
            Log.e("FullScReminder", "Invalid millisId received.")
            finish()
            return
        }

        // Set click listeners for buttons with pending intents
        val takenButton = findViewById<Button>(R.id.taken_button)
        val takenIntent = Intent(this, TakenReceiver::class.java)
        takenIntent.putExtra("millisId", millisId)
        val takenPendingIntent = PendingIntent.getBroadcast(this, millisId, takenIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        takenButton.setOnClickListener {
            takenPendingIntent.send()
            finish()
        }

        val skipButton = findViewById<Button>(R.id.skip_button)
        val skipIntent = Intent(this, SkipReceiver::class.java)
        skipIntent.putExtra("millisId", millisId)
        val skipPendingIntent = PendingIntent.getBroadcast(this, millisId, skipIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        skipButton.setOnClickListener {
            skipPendingIntent.send()
            finish()
        }
    }
}