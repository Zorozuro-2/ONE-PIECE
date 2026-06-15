package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.ConfettiOverlay
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.YlagViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: YlagViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val currentTab by viewModel.currentTab.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                    // Screen Router based on VM selected state
                    when (currentTab) {
                        "DASHBOARD" -> DashboardScreen(
                            viewModel = viewModel,
                            innerPadding = androidx.compose.foundation.layout.PaddingValues()
                        )
                        "HABITS" -> HabitScreen(
                            viewModel = viewModel,
                            innerPadding = androidx.compose.foundation.layout.PaddingValues()
                        )
                        "TASKS" -> TaskScreen(
                            viewModel = viewModel,
                            innerPadding = androidx.compose.foundation.layout.PaddingValues()
                        )
                        "JOURNAL" -> JournalScreen(
                            viewModel = viewModel,
                            innerPadding = androidx.compose.foundation.layout.PaddingValues()
                        )
                        "PROFILE" -> ProfileScreen(
                            viewModel = viewModel,
                            innerPadding = androidx.compose.foundation.layout.PaddingValues()
                        )
                        else -> DashboardScreen(
                            viewModel = viewModel,
                            innerPadding = androidx.compose.foundation.layout.PaddingValues()
                        )
                    }

                    // Particle Burst Overlay (Top-level glow burst trigger)
                    ConfettiOverlay(trigger = viewModel.triggerConfetti)
                }
            }
        }
    }
}
