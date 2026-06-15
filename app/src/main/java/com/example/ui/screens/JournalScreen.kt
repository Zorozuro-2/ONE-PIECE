package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MoodEntry
import com.example.ui.components.CyberZenLayout
import com.example.ui.theme.*
import com.example.ui.viewmodel.YlagViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalScreen(
    viewModel: YlagViewModel,
    innerPadding: PaddingValues
) {
    val moodsList by viewModel.moodEntries.collectAsState()
    
    // Quick ADD states
    var selectedEmoji by remember { mutableStateOf("🍖") }
    var moodScoreSlider by remember { mutableStateOf(4f) }
    var energyScoreSlider by remember { mutableStateOf(4f) }
    var reflectiveNotesText by remember { mutableStateOf("") }

    val emojis = listOf(
        "🍖" to "Feed",
        "⚡" to "Gear 5",
        "😌" to "Sailing",
        "😐" to "Calm",
        "😴" to "Snoozing",
        "😔" to "Defeated",
        "☠️" to "Pirate Fight"
    )

    CyberZenLayout(
        viewModel = viewModel,
        title = "Captain's logbook"
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // MOOD & ENERGY TREND GRAPH (Custom Drawn Bezier Chart)
            item {
                Text(
                    text = "7-DAY SPIRIT & WILLPOWER TREND",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                MoodTrendChart(moodEntries = moodsList)
            }

            // JOURNAL INPUT BLOCK
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGreen, RoundedCornerShape(24.dp))
                        .testTag("log_mood_section"),
                    colors = CardDefaults.cardColors(containerColor = DarkCard),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "LOG CAPTAIN STATE",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )

                        // Emoji Picker
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            emojis.forEach { (emoji, label) ->
                                val isSelected = selectedEmoji == emoji
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) ForestZen.copy(alpha = 0.5f) else Color.Transparent)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) CyberGreen else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedEmoji = emoji }
                                        .padding(6.dp)
                                        .testTag("emoji_btn_$emoji")
                                ) {
                                    Text(text = emoji, fontSize = 24.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 8.sp, color = TextMuted)
                                }
                            }
                        }

                        // Sliders
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "PIRATE SPIRIT: ${moodScoreSlider.toInt()}/5", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                                Text(text = if(moodScoreSlider >= 4f) "CONQUEROR HAKI" else if(moodScoreSlider >= 3f) "STEADY SAILS" else "ROUGH SEAS", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
                            }
                            Slider(
                                value = moodScoreSlider,
                                onValueChange = { moodScoreSlider = it },
                                valueRange = 1f..5f,
                                steps = 3,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberGreen,
                                    activeTrackColor = CyberGreen,
                                    inactiveTrackColor = BorderGreen
                                ),
                                modifier = Modifier.testTag("wellbeing_slider")
                            )
                        }

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "SAILING ENERGY: ${energyScoreSlider.toInt()}/5", style = MaterialTheme.typography.bodyMedium, color = TextWhite)
                                Text(text = if(energyScoreSlider >= 4f) "GEAR 5 SPEED" else if(energyScoreSlider >= 3f) "DOCKING WORK" else "EXHAUSTED", style = MaterialTheme.typography.labelSmall, color = CyberGreen)
                            }
                            Slider(
                                value = energyScoreSlider,
                                onValueChange = { energyScoreSlider = it },
                                valueRange = 1f..5f,
                                steps = 3,
                                colors = SliderDefaults.colors(
                                    thumbColor = CyberGreen,
                                    activeTrackColor = CyberGreen,
                                    inactiveTrackColor = BorderGreen
                                ),
                                modifier = Modifier.testTag("energy_slider")
                            )
                        }

                        // Reflection Prompt note input
                        OutlinedTextField(
                            value = reflectiveNotesText,
                            onValueChange = { reflectiveNotesText = it },
                            label = { Text("VOYAGE CHRONICLES") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("mood_notes_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberGreen,
                                unfocusedBorderColor = BorderGreen,
                                focusedLabelColor = CyberGreen,
                                unfocusedLabelColor = TextMuted,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            placeholder = { Text("What challenges did your pirate crew encounter today on the Grand Line?", fontSize = 12.sp, color = TextMuted) }
                        )

                        Button(
                            onClick = {
                                viewModel.addMoodEntry(
                                    emoji = selectedEmoji,
                                    moodScore = moodScoreSlider.toInt(),
                                    energyScore = energyScoreSlider.toInt(),
                                    note = reflectiveNotesText
                                )
                                reflectiveNotesText = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = DarkBg),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("save_mood_button")
                        ) {
                            Text("SAVE BIOLOGICAL METRICS", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // RECENT LOGS TITLE
            item {
                Text(
                    text = "HISTORICAL INTEGRATION LOGS",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // RECENT LOGS LOOP
            if (moodsList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO CALIBRATIONS LOGGED",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else {
                items(moodsList, key = { it.id }) { log ->
                    MoodLogItem(
                        entry = log,
                        onDelete = { viewModel.deleteMoodEntry(log) }
                    )
                }
            }
        }
    }
}

@Composable
fun MoodTrendChart(
    moodEntries: List<MoodEntry>,
    modifier: Modifier = Modifier
) {
    // Take the 7 most recent entries chronologically ascending for the chart
    val chronMoods = remember(moodEntries) {
        moodEntries.take(7).reversed()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .border(1.dp, BorderGreen, RoundedCornerShape(12.dp))
            .background(DarkCard)
            .padding(16.dp)
    ) {
        if (chronMoods.size < 2) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "NEED LOGS TO PRIME CALIBRATION",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Submit at least 2 entries to establish trend.",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }
        } else {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val chartWidth = size.width
                val chartHeight = size.height
                val padding = 12.dp.toPx()

                val lineCount = 5
                // Draw horizontal guide lines
                for (i in 0 until lineCount) {
                    val y = padding + i * (chartHeight - 2 * padding) / (lineCount - 1)
                    drawLine(
                        color = BorderGreen.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(chartWidth, y),
                        strokeWidth = 1f
                    )
                }

                // Plot points
                val sizeRatio = chronMoods.size - 1
                val xSpacing = chartWidth / sizeRatio
                val points = chronMoods.mapIndexed { idx, entry ->
                    val x = idx * xSpacing
                    // Map score 1-5 to target Y coordinate (reversed)
                    val percent = (entry.moodScore - 1f) / 4f
                    val y = padding + (1f - percent) * (chartHeight - 2 * padding)
                    Offset(x, y)
                }

                // Draw connecting path
                val connectionPath = Path().apply {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        val current = points[i]
                        val prev = points[i - 1]
                        val controlX = (prev.x + current.x) / 2
                        cubicTo(controlX, prev.y, controlX, current.y, current.x, current.y)
                    }
                }

                drawPath(
                    path = connectionPath,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            CyberGreen.copy(alpha = 0.5f),
                            CyberGreen
                        )
                    ),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw glowing dots on top
                points.forEachIndexed { idx, pt ->
                    // Dot outer glow
                    drawCircle(
                        color = CyberGreen.copy(alpha = 0.3f),
                        radius = 8.dp.toPx(),
                        center = pt
                    )
                    // Inner solid dot
                    drawCircle(
                        color = CyberGreen,
                        radius = 4.dp.toPx(),
                        center = pt
                    )
                }
            }
            
            // X-Axis labels (Emoji indicators floating beneath points)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                chronMoods.forEach { entry ->
                    Text(
                        text = entry.emoji,
                        fontSize = 12.sp,
                        modifier = Modifier.width(20.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun MoodLogItem(
    entry: MoodEntry,
    onDelete: () -> Unit
) {
    val df = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }
    val formattedDate = remember(entry.timestamp) { df.format(Date(entry.timestamp)) }

    Surface(
        color = DarkCard,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGreen, shape = RoundedCornerShape(24.dp))
            .testTag("mood_item_${entry.id}"),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = entry.emoji, fontSize = 28.sp)
                    Column {
                        Text(
                            text = formattedDate.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = CyberGreen,
                            fontWeight = FontWeight.Bold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "WELLBEING: ${entry.moodScore}/5", style = MaterialTheme.typography.labelSmall, color = TextWhite)
                            Text(text = "ENERGY: ${entry.energyScore}/5", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        }
                    }
                }

                IconButton(
                    onClick = { onDelete() },
                    modifier = Modifier.testTag("delete_mood_${entry.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Calibration Log",
                        tint = ErrorRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            if (entry.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = BorderGreen, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = entry.note,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}
