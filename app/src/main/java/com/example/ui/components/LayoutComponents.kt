package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.YlagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyberZenLayout(
    viewModel: YlagViewModel,
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    val context = LocalContext.current
    val currentTab = viewModel.currentTab.value

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "ONE PIECE",
                            style = MaterialTheme.typography.titleLarge,
                            color = CyberGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "[${title.uppercase()}]",
                            style = MaterialTheme.typography.labelMedium,
                            color = TextWhite,
                            letterSpacing = 0.5.sp
                        )
                    }
                },
                actions = {
                    // Export Button
                    IconButton(
                        onClick = { viewModel.exportLifeHistoryAsJson(context) },
                        modifier = Modifier.testTag("action_export_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export JSON history",
                            tint = CyberGreen
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBg,
                    titleContentColor = TextWhite
                )
            )
        },
        bottomBar = {
            CyberZenBottomNavigation(
                selectedTab = currentTab,
                onTabSelected = { tab -> viewModel.selectTab(tab) }
            )
        },
        containerColor = DarkBg,
        content = { innerPadding ->
            content(innerPadding)
        }
    )
}

@Composable
fun CyberZenBottomNavigation(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Surface(
        color = DarkCard,
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 1.dp, color = BorderGreen, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .windowInsetsPadding(WindowInsets.navigationBars), // Prevent cutting off on modern devices as guided
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(
                label = "GLANCE",
                icon = Icons.Default.Home,
                isSelected = selectedTab == "DASHBOARD",
                onClick = { onTabSelected("DASHBOARD") },
                tag = "nav_dashboard"
            )
            BottomNavItem(
                label = "HABITS",
                icon = Icons.Default.Refresh,
                isSelected = selectedTab == "HABITS",
                onClick = { onTabSelected("HABITS") },
                tag = "nav_habits"
            )
            BottomNavItem(
                label = "TASKS",
                icon = Icons.Default.Check,
                isSelected = selectedTab == "TASKS",
                onClick = { onTabSelected("TASKS") },
                tag = "nav_tasks"
            )
            BottomNavItem(
                label = "JOURNAL",
                icon = Icons.Default.Favorite,
                isSelected = selectedTab == "JOURNAL",
                onClick = { onTabSelected("JOURNAL") },
                tag = "nav_journal"
            )
            BottomNavItem(
                label = "PROFILE",
                icon = Icons.Default.Settings,
                isSelected = selectedTab == "PROFILE",
                onClick = { onTabSelected("PROFILE") },
                tag = "nav_profile"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    tag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .width(64.dp)
            .testTag(tag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) CyberGreen else TextMuted,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) TextWhite else TextMuted,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            letterSpacing = 0.5.sp
        )
    }
}
