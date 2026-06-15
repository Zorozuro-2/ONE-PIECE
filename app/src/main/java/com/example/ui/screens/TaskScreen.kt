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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Task
import com.example.ui.components.CyberZenLayout
import com.example.ui.theme.*
import com.example.ui.viewmodel.YlagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    viewModel: YlagViewModel,
    innerPadding: PaddingValues
) {
    val tasksList by viewModel.tasks.collectAsState()
    var selectedCategory by remember { mutableStateOf("URGENT_IMPORTANT") }
    var showAddDialog by remember { mutableStateOf(false) }
    var taskTitleText by remember { mutableStateOf("") }
    var formCategory by remember { mutableStateOf("URGENT_IMPORTANT") }

    val categories = listOf(
        "URGENT_IMPORTANT" to "Q1: CRITICAL BATTLES",
        "IMPORTANT_NOT_URGENT" to "Q2: HAKI TRAINING",
        "URGENT_NOT_IMPORTANT" to "Q3: SHIPWORK DECK",
        "NOT_URGENT_NOT_IMPORTANT" to "Q4: TRIVIAL FEASTS"
    )

    CyberZenLayout(
        viewModel = viewModel,
        title = "Battle priorities"
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkBg)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // MATRIX OVERVIEW GRIDS (2x2 Interactive Squares)
                Text(
                    text = "BATTLE PRIORITIZATION COMPASS",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BorderGreen, RoundedCornerShape(24.dp))
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        MatrixQuadrant(
                            num = "Q1",
                            desc = "BATTLES",
                            taskCount = tasksList.count { it.matrixCategory == "URGENT_IMPORTANT" && !it.isCompleted },
                            isSelected = selectedCategory == "URGENT_IMPORTANT",
                            color = ErrorRed,
                            onClick = { selectedCategory = "URGENT_IMPORTANT" },
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(color = BorderGreen, thickness = 1.dp)
                        MatrixQuadrant(
                            num = "Q2",
                            desc = "AMBITION",
                            taskCount = tasksList.count { it.matrixCategory == "IMPORTANT_NOT_URGENT" && !it.isCompleted },
                            isSelected = selectedCategory == "IMPORTANT_NOT_URGENT",
                            color = YellowWarning,
                            onClick = { selectedCategory = "IMPORTANT_NOT_URGENT" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    HorizontalDivider(color = BorderGreen, thickness = 1.dp)
                    Row(modifier = Modifier.fillMaxWidth().height(100.dp)) {
                        MatrixQuadrant(
                            num = "Q3",
                            desc = "CHORES",
                            taskCount = tasksList.count { it.matrixCategory == "URGENT_NOT_IMPORTANT" && !it.isCompleted },
                            isSelected = selectedCategory == "URGENT_NOT_IMPORTANT",
                            color = Color(0xFF42A5F5),
                            onClick = { selectedCategory = "URGENT_NOT_IMPORTANT" },
                            modifier = Modifier.weight(1f)
                        )
                        VerticalDivider(color = BorderGreen, thickness = 1.dp)
                        MatrixQuadrant(
                            num = "Q4",
                            desc = "FEASTS",
                            taskCount = tasksList.count { it.matrixCategory == "NOT_URGENT_NOT_IMPORTANT" && !it.isCompleted },
                            isSelected = selectedCategory == "NOT_URGENT_NOT_IMPORTANT",
                            color = TextMuted,
                            onClick = { selectedCategory = "NOT_URGENT_NOT_IMPORTANT" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // HEADER OF SELECTED CATEGORY + CLEAR COMPLETED
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = categories.find { it.first == selectedCategory }?.second ?: "ACTIVE ITEMS",
                        style = MaterialTheme.typography.labelMedium,
                        color = CyberGreen,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    
                    TextButton(
                        onClick = { viewModel.clearCompletedTasks() },
                        modifier = Modifier.testTag("clear_completed_button")
                    ) {
                        Text(
                            text = "CLEAR COMPLETED",
                            style = MaterialTheme.typography.labelSmall,
                            color = ErrorRed.copy(alpha = 0.8f),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // TASKS OF SELECTED QUADRANT
                val filteredTasks = tasksList.filter { it.matrixCategory == selectedCategory }

                if (filteredTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "VACANT STATE. NO BATTLE PRIORITIES LOGGED.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted,
                            fontFamily = FontFamily.SansSerif,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(filteredTasks, key = { it.id }) { task ->
                            TaskListItem(
                                task = task,
                                onToggle = { viewModel.toggleTask(task) },
                                onDelete = { viewModel.deleteTask(task) }
                            )
                        }
                    }
                }
            }

            // FLOATING ACTION BUTTON
            FloatingActionButton(
                onClick = { 
                    formCategory = selectedCategory
                    showAddDialog = true 
                },
                containerColor = CyberGreen,
                contentColor = DarkBg,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 20.dp)
                    .testTag("add_task_fab"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Queue Task"
                )
            }
        }

        // Add Task Dialog
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = {
                    Text(
                        text = "DECLARE BATTLE OBJECTIVE",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextWhite,
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = taskTitleText,
                            onValueChange = { taskTitleText = it },
                            label = { Text("TASK DESCRIPTION") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("task_title_input"),
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
                                text = "DECISION QUADRANT",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            categories.forEach { (catKey, catLabel) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { formCategory = catKey }
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = formCategory == catKey,
                                        onClick = { formCategory = catKey },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = CyberGreen,
                                            unselectedColor = BorderGreen
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = catLabel,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = if (formCategory == catKey) TextWhite else TextMuted
                                    )
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (taskTitleText.isNotBlank()) {
                                viewModel.addTask(taskTitleText, formCategory)
                                taskTitleText = ""
                                showAddDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberGreen, contentColor = DarkBg),
                        modifier = Modifier.testTag("save_task_button")
                    ) {
                        Text("QUEUE")
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
fun MatrixQuadrant(
    num: String,
    desc: String,
    taskCount: Int,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .background(if (isSelected) color.copy(alpha = 0.08f) else DarkCard)
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("quadrant_trigger_${num.lowercase()}")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = num,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) color else TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                if (taskCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(color.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$taskCount ACT",
                            style = MaterialTheme.typography.labelSmall,
                            color = color,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = desc,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) TextWhite else TextMuted,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun TaskListItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = DarkCard,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGreen, shape = RoundedCornerShape(24.dp))
            .testTag("task_item_${task.id}"),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
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
                    checked = task.isCompleted,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = CyberGreen,
                        uncheckedColor = BorderGreen,
                        checkmarkColor = DarkBg
                    ),
                    modifier = Modifier.testTag("task_checkbox_${task.id}")
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (task.isCompleted) TextMuted else TextWhite,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                )
            }

            IconButton(
                onClick = { onDelete() },
                modifier = Modifier.testTag("delete_task_${task.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete task entry",
                    tint = ErrorRed.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
