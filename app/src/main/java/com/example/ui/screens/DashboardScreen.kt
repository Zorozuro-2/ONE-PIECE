package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.YlagViewModel

@Composable
fun DashboardScreen(
    viewModel: YlagViewModel,
    innerPadding: PaddingValues
) {
    val profileState by viewModel.profile.collectAsState()
    val habitsList by viewModel.habits.collectAsState()
    val tasksList by viewModel.tasks.collectAsState()
    val rawScore by viewModel.dopamineScore.collectAsState()
    val tipState by viewModel.growthTip.collectAsState()
    val questionState by viewModel.questionOfTheDay.collectAsState()

    val scrollState = rememberScrollState()

    // Calculate completions per pillar
    val healthHabits = habitsList.filter { it.pillar == "HEALTH" }
    val healthCompleted = healthHabits.count { viewModel.isToday(it.lastCompletedTimestamp) }

    val wealthHabits = habitsList.filter { it.pillar == "WEALTH" }
    val wealthCompleted = wealthHabits.count { viewModel.isToday(it.lastCompletedTimestamp) }

    val wisdomHabits = habitsList.filter { it.pillar == "WISDOM" }
    val wisdomCompleted = wisdomHabits.count { viewModel.isToday(it.lastCompletedTimestamp) }

    val happinessHabits = habitsList.filter { it.pillar == "HAPPINESS" }
    val happinessCompleted = happinessHabits.count { viewModel.isToday(it.lastCompletedTimestamp) }

    CyberZenLayout(
        viewModel = viewModel,
        title = "Glance"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Welcome Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "CAPTAIN ${profileState?.name?.uppercase() ?: "MUGIWARA"}",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberGreen,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Grand Line Pirate Ledger",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted
                    )
                }
            }

            // Glow Ring section (Dopamine Score)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCard, RoundedCornerShape(24.dp))
                    .border(1.dp, BorderGreen, RoundedCornerShape(24.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlowingDopamineRing(score = rawScore)

                Column(
                    modifier = Modifier.width(150.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "CREW WILLPOWER",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (rawScore >= 90) "CONQUEROR HAKI BURST" else if (rawScore >= 50) "SAILING SMOOTH" else "ANCHORED IN HARBOR",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (rawScore >= 90) CyberGreen else TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Perform habits and tasks today to raise your score.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            // Supportive Growth Tip (if score is below 50)
            AnimatedVisibility(
                visible = rawScore < 50 && tipState.isNotEmpty(),
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(ForestZen.copy(alpha = 0.25f))
                        .border(1.dp, CyberGreen.copy(alpha = 0.40f), RoundedCornerShape(24.dp))
                        .padding(14.dp)
                        .testTag("growth_tip_panel")
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Growth Tip",
                            tint = CyberGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "SUPPORT COGNITION",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = tipState,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            // Memento Mori bar
            LifeProgressBar(
                age = profileState?.age ?: 28,
                lifeExpectancy = profileState?.lifeExpectancy ?: 80
            )

            // DUOLINGO-THEMED STRAW HAT MASCOT & ALARM UTILITY PANEL
            val localContext = LocalContext.current
            val alarmTimeState by viewModel.alarmTime.collectAsState()
            val alarmStatusActive by viewModel.alarmEnabledStatus.collectAsState()

            var alarmHourInput by remember { mutableStateOf(8f) }
            var alarmMinuteInput by remember { mutableStateOf(30f) }
            var usePhoneClockIntent by remember { mutableStateOf(true) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGreen, RoundedCornerShape(24.dp))
                    .testTag("mascot_alarm_panel_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header of the Panel
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "STRAW HAT MASCOT REMINDERS",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (alarmStatusActive) ForestZen.copy(alpha = 0.2f) else BorderGreen)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (alarmStatusActive) "ALARM: ACTIVE ($alarmTimeState)" else "ALARM: INACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (alarmStatusActive) CyberGreen else TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Duolingo-Style Animated Mascot Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkBg.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .border(1.dp, BorderGreen, RoundedCornerShape(16.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Infinite hovering bouncing loop for Duolingo-style animation excitement
                        val infiniteTransition = rememberInfiniteTransition(label = "mascot_hover")
                        val hoverOffset by infiniteTransition.animateFloat(
                            initialValue = -8f,
                            targetValue = 8f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1200, easing = FastOutSlowInEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "hover_offset"
                        )
                        val rotationOffset by infiniteTransition.animateFloat(
                            initialValue = -4f,
                            targetValue = 4f,
                            animationSpec = infiniteRepeatable(
                                animation = tween(1000, easing = LinearEasing),
                                repeatMode = RepeatMode.Reverse
                            ),
                            label = "rotate_offset"
                        )

                        // Visual Mascot based on active dopamine streak state
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .offset(y = hoverOffset.dp)
                                .drawBehind {
                                    drawCircle(
                                        color = when {
                                            rawScore >= 90 -> CyberGreen.copy(alpha = 0.25f)
                                            rawScore >= 50 -> ForestZen.copy(alpha = 0.25f)
                                            else -> ErrorRed.copy(alpha = 0.20f)
                                        },
                                        radius = size.width / 1.6f
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                when {
                                    rawScore >= 90 -> { // GEAR 5 JOY BOY (Glowing Aura effect)
                                        Text(text = "⚡😆⚡", fontSize = 28.sp)
                                        Text(
                                            text = "GEAR 5",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = CyberGreen,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 8.sp
                                        )
                                    }
                                    rawScore >= 50 -> { // HAPPY STRAW HAT LUFFY
                                        Text(text = "🍖😎👒", fontSize = 26.sp)
                                        Text(
                                            text = "STEADY",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = TextWhite,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    }
                                    else -> { // CRYING / ANGRY DUOLINGO Reminders
                                        Text(text = "😭💢☠️", fontSize = 26.sp)
                                        Text(
                                            text = "STREAK LOW",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = ErrorRed,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 8.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Speech Dialogue bubble
                        Column(modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DarkCard)
                                    .border(1.dp, BorderGreen, RoundedCornerShape(12.dp))
                                    .padding(8.dp)
                            ) {
                                val bubbleMessage = when {
                                    rawScore >= 90 -> "“Hahaha! I can hear the Drums of Liberation! Your will is insane! Let's sail to Laughtale!”"
                                    rawScore >= 50 -> "“Sailing steady. We have active winds! Keep completing your habits so Zoro doesn't mock you!”"
                                    else -> "“Oi Nakama! Your pirate spirit is fading! Complete your chores and train your Haki! S-E-T ALARMS! ⏰”"
                                }
                                Text(
                                    text = bubbleMessage,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextWhite,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }

                    // Configuration Sliders
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "CONFIGURE DAILY TASK & ROUTINE ALARM",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            fontWeight = FontWeight.Bold
                        )

                        // Hour selector
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Hour: ${alarmHourInput.toInt()}:00",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite
                            )
                            Text(
                                text = if (alarmHourInput >= 12) "PM" else "AM",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = alarmHourInput,
                            onValueChange = { alarmHourInput = it },
                            valueRange = 0f..23f,
                            colors = SliderDefaults.colors(
                                thumbColor = CyberGreen,
                                activeTrackColor = CyberGreen,
                                inactiveTrackColor = BorderGreen
                            ),
                            modifier = Modifier.testTag("alarm_hour_slider")
                        )

                        // Minute selector
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Minutes: ${alarmMinuteInput.toInt()}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextWhite
                            )
                        }
                        Slider(
                            value = alarmMinuteInput,
                            onValueChange = { alarmMinuteInput = it },
                            valueRange = 0f..59f,
                            colors = SliderDefaults.colors(
                                thumbColor = CyberGreen,
                                activeTrackColor = CyberGreen,
                                inactiveTrackColor = BorderGreen
                            ),
                            modifier = Modifier.testTag("alarm_minute_slider")
                        )

                        // Connection checkbox option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = usePhoneClockIntent,
                                onCheckedChange = { usePhoneClockIntent = it },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = CyberGreen,
                                    uncheckedColor = BorderGreen,
                                    checkmarkColor = DarkBg
                                )
                            )
                            Column {
                                Text(
                                    text = "Connect to Phone Alarm App",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextWhite,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Creates physical recurring alarm inside default Android Clock App",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.scheduleDailyAlarm(
                                    context = localContext,
                                    hour = alarmHourInput.toInt(),
                                    minute = alarmMinuteInput.toInt(),
                                    connectToPhoneClock = usePhoneClockIntent
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "ACTIVATE ⏰",
                                color = DarkBg,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.disableActiveAlarm(localContext)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ForestZen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "DISABLE 🔕",
                                color = TextWhite,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Instant Push Demo Button
                    Button(
                        onClick = {
                            viewModel.triggerInstantDemoNotification(localContext)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BorderGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "⚡ TEST DUOLINGO PIRATE PUSH INSTANTLY",
                            color = TextWhite,
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            // Dynamic Question of the Day Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BorderGreen, RoundedCornerShape(24.dp))
                    .testTag("stoic_question_card"),
                colors = CardDefaults.cardColors(containerColor = DarkCard),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = questionState.second.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        IconButton(
                            onClick = { viewModel.updateQuestion() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Next prompt",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${questionState.first}\"",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Normal,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 22.sp
                    )
                }
            }

            // Four pillars progress matrix
            QuadrantGrid(
                healthCompleted = healthCompleted, healthTotal = healthHabits.size,
                wealthCompleted = wealthCompleted, wealthTotal = wealthHabits.size,
                wisdomCompleted = wisdomCompleted, wisdomTotal = wisdomHabits.size,
                happinessCompleted = happinessCompleted, happinessTotal = happinessHabits.size
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
