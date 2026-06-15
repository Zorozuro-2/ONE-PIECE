package com.example.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import com.example.R
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PirateCrewWidget : AppWidgetProvider() {

    private val widgetScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Since database access is asynchronous, we do a coroutine launch and update widgets
        widgetScope.launch {
            val db = AppDatabase.getDatabase(context)
            val dao = db.ylagDao
            
            // Fetch live user metrics
            val profile = dao.getUserProfile()
            val habitsList = try {
                val flow = dao.getAllHabitsFlow()
                // Just as a helper, or fetch synchronously
                // To avoid flows blocking, we can fetch tasks and calculate completion
                listOf<String>()
            } catch (e: Exception) {
                listOf<String>()
            }
            
            val captainName = profile?.name ?: "Monkey D. Luffy"
            val age = profile?.age ?: 19
            val expectancy = profile?.lifeExpectancy ?: 100
            val pct = if (expectancy > 0) (age * 100) / expectancy else 19

            // Random Duolingo Pirate Mascot interactive phrase
            val notifications = listOf(
                "“Oi Nakama! Train your Conqueror Haki today; no slacking!”",
                "“Sailing 100% on track! Ensure you eat some meat & train!”",
                "“The Pirate King doesn't miss daily workouts! Hoist sails!”",
                "“Sunny is ready to launch. Complete daily prioritizations!”",
                "“Haki requires absolute focus. Mark your routines complete!”",
                "“A true nakama always fulfills their daily pirate vows!”"
            )
            val randomPhrase = notifications.shuffled().first()

            for (appWidgetId in appWidgetIds) {
                val views = RemoteViews(context.packageName, R.layout.widget_pirate_crew)
                
                // Bind real data to widget view IDs
                views.setTextViewText(R.id.widget_title, "★ ONE PIECE LEDGER")
                views.setTextViewText(R.id.widget_streak, "CAPTAIN: $captainName")
                views.setTextViewText(R.id.widget_mascot_text, randomPhrase)
                views.setTextViewText(R.id.widget_progress_text, "Grand Line: $age.0 / $expectancy YRS ($pct%)")
                
                // Static counts for the preview dashboard widget
                views.setTextViewText(R.id.widget_task_summary, "SAILING ACTIVE ⛵")

                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
        }
    }
}
