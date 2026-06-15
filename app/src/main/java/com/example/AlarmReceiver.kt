package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.ui.components.SecurityValidator

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.i("SECURITY_AUDIT", "AlarmReceiver caught intent action: $action")
        
        if (action == "com.example.ACTION_DAILY_ALARM" || action == Intent.ACTION_BOOT_COMPLETED) {
            // Trigger Duolingo One-Piece styled reminder notifications
            triggerPiratePush(context)
        }
    }

    private fun triggerPiratePush(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "straw_hat_reminders_channel"
        val channelName = "Straw Hat Voyage Reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily alerts and willpower reminders under the pirate banner"
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Duolingo styled motivational & slightly dramatic statements
        val notifications = listOf(
            "“Oi Nakama! Luffy is waiting on deck! Hoist the sails and complete your daily goals now!”" to "☠️ BATTLE CALL, NAKAMA!",
            "“Are you slacking off? Zoro completed his training 3 hours ago! Log your willpower now!”" to "🔥 ZORO'S PRIDE INSULT!",
            "“Keep your dream streak alive or suffer a 100M bounty drop! Act now of your own volition!”" to "💰 THE BOUNTY STREAK ALERT!",
            "“Don't let your daily spirit fall in Davey Jones' locker! Complete your prioritizations!”" to "⚓ CAPTAIN'S DAILY ORDERS!"
        )
        val selected = notifications.shuffled().first()
        val text = selected.first
        val title = selected.second

        // Open app on click
        val clickIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Safe standard notification vector icon
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            notificationManager.notify(4890, builder.build())
            SecurityValidator.logSecurityAttempt("ALARM_TRIGGER", true, "Successfully posted daily notification alert: $title")
        } catch (e: Exception) {
            Log.e("SECURITY_AUDIT", "Failed to trigger push notice alert", e)
        }
    }
}
