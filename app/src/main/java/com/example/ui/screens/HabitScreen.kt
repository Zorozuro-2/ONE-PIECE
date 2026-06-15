package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Habit
import com.example.ui.components.CyberZenLayout
import com.example.ui.theme.*
import com.example.ui.viewmodel.YlagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitScreen(
    viewModel: YlagViewModel,
    innerPadding: PaddingValues
) {
    val habitsList by viewModel.habits.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var habitNameText by remember { mutableStateOf("") }
    var selectedPillar by remember { mutableStateOf("HEALTH") }

    val pillars = listOf("HEALTH", "WEALTH", "WISDOM", "HAPPINESS")

    CyberZenLayout(
        viewModel = viewModel,
        title = "Habits"
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            if (habitsList.isEmpty()) {
                // Empty State representation
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "NO VOYAGE ROUTINES SET",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Hoisted sails are waiting! Establish your daily routines and conqueror Haki exercises in one of the four pirate attributes.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextMuted,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    // Group habits by pillar
                    pillars.forEach { pillar ->
                        val pillarHabits = habitsList.filter { it.pillar == pillar }
                        if (pillarHabits.isNotEmpty()) {
                            item {
                                val displayPillar = when (pillar) {
                                    "HEALTH" -> "HEALTH (HAKI)"
                                    "WEALTH" -> "WEALTH (BOUNTY)"
                                    "WISDOM" -> "WISDOM (PONEGLYPH)"
                                    "HAPPINESS" -> "HAPPINESS (NAKAMA)"
                                    else -> pillar
                                }
                                Text(
                                    text = displayPillar,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = when (pillar) {
                                        "HEALTH" -> ErrorRed
                                        "WEALTH" -> YellowWarning
                                        "WISDOM" -> CyberGreen
                                        else -> Color(0xFF42A5F5)
                                    },
                                    fontFamily = FontFamily.SansSerif,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            
                            items(pillarHabits, key = { it.id }) { habit ->
                                val isCompleted = viewModel.isToday(habit.lastCompletedTimestamp)
                                HabitListItem(
                                    habit = habit,
                                    isCompleted = isCompleted,
                                    onToggle = { viewModel.toggleHabit(habit) },
                                    onDelete = { viewModel.deleteHabit(habit) }
                                )
                            }
                        }
                    }
                }
            }

            // FLOATING ACTION BUTTON FOR ADDING
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = CyberGreen,
                contentColor = DarkBg,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
                    .testTag("add_habit_fab"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new Habit"
                )
            }
        }

        // Add Habit Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Text(
                        text = "INITIATE NEW LOOP",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = habitNameText,
                            onValueChange = { habitNameText = it },
                            label = { Text("HABIT DESCRIPTION") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("habit_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGreen,
                                unfocusedBorderColor = BorderGreen,
                                focusedLabelColor = CyberGreen,
                                unfocusedLabelColor = TextMuted,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true
                        )

                        Column {
                            Text(
                                text = "CORE PILLAR",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                pillars.forEach { pillar ->
                                    val isSelected = selectedPillar == pillar
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(if (isSelected) ForestZen else BorderGreen)
                                            .border(
                                                width = 1.dp,
                                                color = if (isSelected) CyberGreen else Color.Transparent,
                                                shape = RoundedCornerShape(6.dp)
                                            )
                                            .clickable { selectedPillar = pillar }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = pillar.take(4),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (isSelected) TextWhite else TextMuted,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (habitNameText.isNotBlank()) {
                                viewModel.addHabit(habitNameText, selectedPillar)
                                habitNameText = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = DarkBg),
                        modifier = Modifier.testTag("save_habit_button")
                    ) {
                        Text("SAVE")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("CANCEL", color = TextMuted)
                    }
                },
                containerColor = DarkCard,
                textContentColor = TextWhite
            )
        }
    }
}

@Composable
fun HabitListItem(
    habit: Habit,
    isCompleted: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = DarkCard,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGreen, shape = RoundedCornerShape(24.dp))
            .testTag("habit_item_${habit.id}"),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onToggle() }
            ) {
                Checkbox(
                    checked = isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = CyberGreen,
                        uncheckedColor = BorderGreen,
                        checkmarkColor = DarkBg
                    ),
                    modifier = Modifier.testTag("habit_checkbox_${habit.id}")
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = habit.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isCompleted) TextMuted else TextWhite,
                        fontWeight = FontWeight.Medium,
                        textDecoration = if (isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Flame Streak Indicator
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(ForestZen.copy(alpha = 0.3f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🔥 ${habit.streak} DAY STREAK",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyberGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Text(
                            text = "TOTAL: ${habit.totalCompletions}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted
                        )
                    }
                }
            }

            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.testTag("delete_habit_${habit.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Habit",
                    tint = ErrorRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
