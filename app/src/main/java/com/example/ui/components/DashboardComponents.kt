package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GlowingDopamineRing(
    score: Int,
    modifier: Modifier = Modifier
) {
    val animatedScore by animateFloatAsState(
        targetValue = score.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "scoreRing"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(170.dp)
            .testTag("dopamine_score_ring")
    ) {
        // Draw the neon circle Canvas background
        Canvas(modifier = Modifier.size(150.dp)) {
            val strokeWidth = 14.dp.toPx()
            
            // Background track
            drawCircle(
                color = BorderGreen,
                radius = size.minDimension / 2 - strokeWidth,
                style = Stroke(width = strokeWidth)
            )
            
            // Glowing arc foreground
            val sweepAngle = (animatedScore / 100f) * 360f
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        CyberGreen.copy(alpha = 0.5f),
                        CyberGreen,
                        CyberGreen.copy(alpha = 0.8f)
                    )
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(strokeWidth, strokeWidth),
                size = Size(size.width - strokeWidth * 2, size.height - strokeWidth * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${score}%",
                style = MaterialTheme.typography.displayMedium,
                color = TextWhite,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "DOPAMINE",
                style = MaterialTheme.typography.labelSmall,
                color = CyberGreen,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun LifeProgressBar(
    age: Int,
    lifeExpectancy: Int,
    modifier: Modifier = Modifier
) {
    val totalWeeks = lifeExpectancy * 52
    val livedWeeks = age * 52
    val remainingWeeks = (totalWeeks - livedWeeks).coerceAtLeast(0)
    val livedPercentage = (age.toFloat() / lifeExpectancy.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BorderGreen, RoundedCornerShape(24.dp))
            .testTag("life_progress_card"),
        colors = CardDefaults.cardColors(containerColor = DarkCard)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "ONE PIECE",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "GRAND LINE VOYAGE",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyberGreen,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Beautiful custom high-precision progress track bar right from the geometric balance theme HTML
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1A1A))
                    .border(1.dp, Color(0xFF2A2A2A), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Progress Fill with horizontal gradient
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(livedPercentage)
                        .align(Alignment.CenterStart)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF064E3B), CyberGreen)
                            )
                        )
                )

                // Overlapping Centered Text
                Text(
                    text = "${age}.0 / ${lifeExpectancy} YEARS (${(livedPercentage * 100).toInt()}%)",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You have sailed ${(livedPercentage * 100).toInt()}% of your Grand Line voyage with ~${remainingWeeks} weeks remaining. Chart your course clearly, gather your crew, and navigate with Haki!",
                style = MaterialTheme.typography.bodyMedium,
                color = TextMuted,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun QuadrantGrid(
    healthCompleted: Int, healthTotal: Int,
    wealthCompleted: Int, wealthTotal: Int,
    wisdomCompleted: Int, wisdomTotal: Int,
    happinessCompleted: Int, happinessTotal: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "THE FOUR PILLARS AT A GLANCE",
            style = MaterialTheme.typography.labelMedium,
            color = TextMuted,
            letterSpacing = 1.sp,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuadrantCard(
                title = "HEALTH",
                completed = healthCompleted,
                total = healthTotal,
                icon = Icons.Default.Favorite,
                iconColor = ErrorRed,
                modifier = Modifier.weight(1f)
            )
            QuadrantCard(
                title = "WEALTH",
                completed = wealthCompleted,
                total = wealthTotal,
                icon = Icons.Default.Star,
                iconColor = YellowWarning,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuadrantCard(
                title = "WISDOM",
                completed = wisdomCompleted,
                total = wisdomTotal,
                icon = Icons.Default.Info,
                iconColor = CyberGreen,
                modifier = Modifier.weight(1f)
            )
            QuadrantCard(
                title = "HAPPINESS",
                completed = happinessCompleted,
                total = happinessTotal,
                icon = Icons.Default.Warning, // Can substitute other material icon if extended not present
                iconColor = Color(0xFF42A5F5),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun QuadrantCard(
    title: String,
    completed: Int,
    total: Int,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    val progress = if (total == 0) 0f else (completed.toFloat() / total.toFloat())
    val percentage = (progress * 100).toInt()

    Box(
        modifier = modifier
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(24.dp))
            .background(DarkCard)
            .border(1.dp, BorderGreen, RoundedCornerShape(24.dp))
            .testTag("quadrant_${title.lowercase()}_card")
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = CyberGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    val displayTitle = when (title.uppercase()) {
                        "HEALTH" -> "HEALTH (HAKI)"
                        "WEALTH" -> "WEALTH (BOUNTY)"
                        "WISDOM" -> "WISDOM (PONEGLYPH)"
                        "HAPPINESS" -> "HAPPINESS (NAKAMA)"
                        else -> title
                    }
                    Text(
                        text = displayTitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 0.5.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$percentage",
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextWhite,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "%",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextMuted,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Column {
                Text(
                    text = if (total == 0) "0/0" else "$completed/$total DONE",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Exact slim elegant horizontal bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BorderGreen)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .clip(RoundedCornerShape(2.dp))
                            .background(CyberGreen)
                    )
                }
            }
        }
    }
}
